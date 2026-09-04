package com.example.ime

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.NXVApplication
import com.example.data.entity.ClipboardItem
import com.example.data.entity.SavedCredential
import com.example.data.preferences.KeyboardSettings
import com.example.keyboard.KeyboardMode
import com.example.keyboard.ShiftState
import com.example.language.avro.AvroPhoneticEngine
import com.example.suggestion.SuggestionEngine
import com.example.suggestion.SuggestionItem
import com.example.ui.keyboard.AutoSavePromptData
import com.example.ui.keyboard.KeyboardRootView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NXVInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val inputConnectionManager = InputConnectionManager()
    private val avroEngine = AvroPhoneticEngine()
    private lateinit var suggestionEngine: SuggestionEngine
    private lateinit var feedbackManager: FeedbackManager

    // Observable states
    private var currentMode by mutableStateOf(KeyboardMode.ENGLISH)
    private var shiftState by mutableStateOf(ShiftState.OFF)
    private var enterActionLabel by mutableStateOf("↵")

    private val suggestionsState = MutableStateFlow<List<SuggestionItem>>(emptyList())
    private val clipboardItemsState = MutableStateFlow<List<ClipboardItem>>(emptyList())
    private val savedCredentialsState = MutableStateFlow<List<SavedCredential>>(emptyList())
    private val currentSettingsState = MutableStateFlow(KeyboardSettings())

    private var autoSavePromptState by mutableStateOf<AutoSavePromptData?>(null)

    // Password & account detection tracking
    private var currentPackageName: String = ""
    private var currentAppName: String = ""
    private var lastObservedUsername: String = ""
    private var currentPasswordBuffer = StringBuilder()
    private var isCurrentFieldPassword: Boolean = false

    // Avro composing buffer
    private val avroRawBuffer = StringBuilder()

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        feedbackManager = FeedbackManager(this)
        val app = application as? NXVApplication ?: NXVApplication.instance
        suggestionEngine = SuggestionEngine(app.repository)

        // Observe Settings
        serviceScope.launch {
            app.preferences.settingsFlow.collectLatest { settings ->
                currentSettingsState.value = settings
                updateModeForLanguage(settings.currentLanguage)
            }
        }

        // Observe Clipboard
        serviceScope.launch {
            app.repository.clipboardItems.collectLatest { items ->
                clipboardItemsState.value = items
            }
        }

        // Observe Credentials
        serviceScope.launch {
            app.repository.savedCredentials.collectLatest { creds ->
                savedCredentialsState.value = creds
            }
        }
    }

    override fun onInitializeInterface() {
        super.onInitializeInterface()
        window?.window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(this)
            decor.setViewTreeViewModelStoreOwner(this)
            decor.setViewTreeSavedStateRegistryOwner(this)
        }
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onShowInputRequested(flags: Int, configChange: Boolean): Boolean {
        return true
    }

    override fun onWindowShown() {
        super.onWindowShown()
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    private var inputComposeView: ComposeView? = null

    override fun onCreateInputView(): View {
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        window?.window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(this)
            decor.setViewTreeViewModelStoreOwner(this)
            decor.setViewTreeSavedStateRegistryOwner(this)
        }

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@NXVInputMethodService))
            setViewTreeLifecycleOwner(this@NXVInputMethodService)
            setViewTreeViewModelStoreOwner(this@NXVInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@NXVInputMethodService)
            setContent {
                val settings by currentSettingsState.collectAsState()
                val suggestions by suggestionsState.collectAsState()
                val clipboardItems by clipboardItemsState.collectAsState()
                val savedCredentials by savedCredentialsState.collectAsState()

                KeyboardRootView(
                    settings = settings,
                    currentMode = currentMode,
                    shiftState = shiftState,
                    suggestions = suggestions,
                    clipboardItems = clipboardItems,
                    savedCredentials = savedCredentials,
                    autoSavePrompt = autoSavePromptState,
                    enterLabel = enterActionLabel,
                    feedbackManager = feedbackManager,
                    onCharTyped = { char -> handleCharTyped(char) },
                    onDelete = { handleDelete() },
                    onSpace = { handleSpace() },
                    onEnter = { handleEnter() },
                    onShiftToggle = { handleShiftToggle() },
                    onModeSwitch = { mode -> currentMode = mode },
                    onLanguageCycle = { cycleLanguage() },
                    onLanguageSelected = { lang -> selectLanguage(lang) },
                    onSuggestionClicked = { item -> handleSuggestionClicked(item) },
                    onPasteClipboard = { text -> handlePaste(text) },
                    onTogglePinClipboard = { item ->
                        serviceScope.launch {
                            val app = application as? NXVApplication ?: NXVApplication.instance
                            app.repository.togglePinClipboardItem(item)
                        }
                    },
                    onDeleteClipboard = { id ->
                        serviceScope.launch {
                            val app = application as? NXVApplication ?: NXVApplication.instance
                            app.repository.deleteClipboardItem(id)
                        }
                    },
                    onClearClipboard = {
                        serviceScope.launch {
                            val app = application as? NXVApplication ?: NXVApplication.instance
                            app.repository.clearClipboard()
                        }
                    },
                    onSaveCredential = { service, user, pass ->
                        serviceScope.launch {
                            val app = application as? NXVApplication ?: NXVApplication.instance
                            app.repository.saveCredential(
                                serviceName = service,
                                username = user,
                                password = pass,
                                packageName = currentPackageName
                            )
                        }
                    },
                    onDeleteCredential = { id ->
                        serviceScope.launch {
                            val app = application as? NXVApplication ?: NXVApplication.instance
                            app.repository.deleteCredential(id)
                        }
                    },
                    onTogglePinCredential = { cred ->
                        serviceScope.launch {
                            val app = application as? NXVApplication ?: NXVApplication.instance
                            app.repository.togglePinCredential(cred)
                        }
                    },
                    onOpenSettings = {
                        val intent = Intent(this@NXVInputMethodService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    },
                    onUpdateOneHanded = { mode ->
                        serviceScope.launch {
                            val app = application as? NXVApplication ?: NXVApplication.instance
                            app.preferences.updateOneHandedMode(mode)
                        }
                    }
                )
            }
        }

        inputComposeView = composeView
        attachLifecycleOwners(composeView)
        return composeView
    }

    private fun attachLifecycleOwners(view: View?) {
        window?.window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(this)
            decor.setViewTreeViewModelStoreOwner(this)
            decor.setViewTreeSavedStateRegistryOwner(this)
        }
        view?.let { v ->
            v.setViewTreeLifecycleOwner(this)
            v.setViewTreeViewModelStoreOwner(this)
            v.setViewTreeSavedStateRegistryOwner(this)
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        attachLifecycleOwners(inputComposeView)
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        inputConnectionManager.update(currentInputConnection, info)
        avroRawBuffer.clear()

        // Extract package & app name
        val pkg = info?.packageName ?: ""
        if (pkg.isNotBlank() && pkg != currentPackageName) {
            currentPackageName = pkg
            currentAppName = try {
                val pm = packageManager
                val appInfo = pm.getApplicationInfo(pkg, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (_: Exception) {
                pkg.substringAfterLast('.').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }

        isCurrentFieldPassword = inputConnectionManager.isPasswordField()
        if (isCurrentFieldPassword) {
            currentPasswordBuffer.clear()
        }

        enterActionLabel = inputConnectionManager.getEnterActionLabel()

        // Adapt layout to input field
        if (inputConnectionManager.isNumberOnlyField()) {
            currentMode = KeyboardMode.NUMBERS
        } else {
            val settings = currentSettingsState.value
            updateModeForLanguage(settings.currentLanguage)
        }

        // Safe clipboard capture
        syncSystemClipboard()

        // Refresh suggestions
        refreshSuggestions()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        commitAvroBuffer()
        checkAndOfferPasswordSave()
        inputConnectionManager.clearComposing()
        avroRawBuffer.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        serviceScope.cancel()
    }

    private fun updateModeForLanguage(lang: String) {
        currentMode = when (lang.lowercase()) {
            "bangla" -> KeyboardMode.BANGLA
            "avro" -> KeyboardMode.AVRO
            else -> KeyboardMode.ENGLISH
        }
    }

    private fun handleCharTyped(char: String) {
        if (isCurrentFieldPassword) {
            currentPasswordBuffer.append(char)
        } else {
            // Track potential email/username input
            val word = inputConnectionManager.getCurrentWordBeforeCursor() + char
            if (word.contains("@") || word.length >= 3) {
                lastObservedUsername = word
            }
        }

        if (currentMode == KeyboardMode.AVRO) {
            avroRawBuffer.append(char)
            val converted = avroEngine.convertWord(avroRawBuffer.toString())
            inputConnectionManager.setComposing(converted, avroRawBuffer.toString())
            refreshSuggestions()
        } else {
            inputConnectionManager.commitText(char)
            // Auto capitalize check
            if (shiftState == ShiftState.ON) {
                shiftState = ShiftState.OFF
            }
            refreshSuggestions()
        }
    }

    private fun handleDelete() {
        if (isCurrentFieldPassword && currentPasswordBuffer.isNotEmpty()) {
            currentPasswordBuffer.deleteCharAt(currentPasswordBuffer.length - 1)
        }

        if (currentMode == KeyboardMode.AVRO && avroRawBuffer.isNotEmpty()) {
            avroRawBuffer.deleteCharAt(avroRawBuffer.length - 1)
            if (avroRawBuffer.isEmpty()) {
                inputConnectionManager.clearComposing()
                inputConnectionManager.handleDelete()
            } else {
                val converted = avroEngine.convertWord(avroRawBuffer.toString())
                inputConnectionManager.setComposing(converted, avroRawBuffer.toString())
            }
            refreshSuggestions()
        } else {
            inputConnectionManager.handleDelete()
            refreshSuggestions()
        }
    }

    private fun handleSpace() {
        if (currentMode == KeyboardMode.AVRO && avroRawBuffer.isNotEmpty()) {
            val converted = avroEngine.convertWord(avroRawBuffer.toString())
            inputConnectionManager.finishComposing(converted)
            learnWord(converted, "bn")
            avroRawBuffer.clear()
        }

        inputConnectionManager.commitText(" ")
        refreshSuggestions()
    }

    private fun handleEnter() {
        if (currentMode == KeyboardMode.AVRO && avroRawBuffer.isNotEmpty()) {
            val converted = avroEngine.convertWord(avroRawBuffer.toString())
            inputConnectionManager.finishComposing(converted)
            learnWord(converted, "bn")
            avroRawBuffer.clear()
        }
        checkAndOfferPasswordSave()
        inputConnectionManager.handleEnter()
        refreshSuggestions()
    }

    private fun checkAndOfferPasswordSave() {
        if (isCurrentFieldPassword && currentPasswordBuffer.length >= 3) {
            val passToSave = currentPasswordBuffer.toString()
            val serviceName = currentAppName.ifBlank { "Account" }
            val username = lastObservedUsername
            val pkg = currentPackageName

            autoSavePromptState = AutoSavePromptData(
                serviceName = serviceName,
                username = username,
                password = passToSave,
                onConfirmSave = {
                    serviceScope.launch {
                        val app = application as? NXVApplication ?: NXVApplication.instance
                        app.repository.saveCredential(
                            serviceName = serviceName,
                            username = username,
                            password = passToSave,
                            packageName = pkg
                        )
                    }
                    autoSavePromptState = null
                },
                onDismiss = {
                    autoSavePromptState = null
                }
            )
            currentPasswordBuffer.clear()
        }
    }

    private fun handleShiftToggle() {
        shiftState = when (shiftState) {
            ShiftState.OFF -> ShiftState.ON
            ShiftState.ON -> ShiftState.CAPS_LOCK
            ShiftState.CAPS_LOCK -> ShiftState.OFF
        }
    }

    private fun handleSuggestionClicked(item: SuggestionItem) {
        inputConnectionManager.replaceCurrentWord(item.replacementText)
        if (currentSettingsState.value.autoSpacing) {
            inputConnectionManager.commitText(" ")
        }
        avroRawBuffer.clear()

        val locale = if (currentMode == KeyboardMode.ENGLISH) "en" else "bn"
        learnWord(item.replacementText, locale)
        refreshSuggestions()
    }

    private fun handlePaste(text: String) {
        inputConnectionManager.commitText(text)
        currentMode = when (currentSettingsState.value.currentLanguage) {
            "bangla" -> KeyboardMode.BANGLA
            "avro" -> KeyboardMode.AVRO
            else -> KeyboardMode.ENGLISH
        }
    }

    private fun commitAvroBuffer() {
        if (avroRawBuffer.isNotEmpty()) {
            val converted = avroEngine.convertWord(avroRawBuffer.toString())
            inputConnectionManager.finishComposing(converted)
            avroRawBuffer.clear()
        }
    }

    private fun cycleLanguage() {
        commitAvroBuffer()
        val settings = currentSettingsState.value
        // Maintain consistent natural toggle order: English -> Bangla -> Avro -> English
        val orderedAll = listOf("english", "bangla", "avro")
        val active = orderedAll.filter { settings.activeLanguages.contains(it) }.ifEmpty { orderedAll }

        val currentIndex = active.indexOf(settings.currentLanguage.lowercase())
        val nextIndex = if (currentIndex == -1 || currentIndex >= active.size - 1) 0 else currentIndex + 1
        val nextLang = active[nextIndex]
        selectLanguage(nextLang)
    }

    private fun selectLanguage(lang: String) {
        commitAvroBuffer()
        serviceScope.launch {
            val app = application as? NXVApplication ?: NXVApplication.instance
            app.preferences.updateCurrentLanguage(lang)
            updateModeForLanguage(lang)
            refreshSuggestions()
        }
    }

    private fun refreshSuggestions() {
        serviceScope.launch {
            val settings = currentSettingsState.value
            val isPass = inputConnectionManager.isPasswordField()

            if (isPass) {
                // In password fields, standard text prediction is disabled for security,
                // but we check Encrypted Storage for matching saved credentials to offer 1-tap autofill!
                val app = application as? NXVApplication ?: NXVApplication.instance
                val matchingCreds = app.repository.getCredentialsForPackageOrQuery(
                    packageName = currentPackageName,
                    query = currentAppName
                )

                if (matchingCreds.isNotEmpty()) {
                    val autofillChips = matchingCreds.take(2).map { cred ->
                        val label = if (cred.username.isNotBlank()) {
                            "🔑 Fill: ${cred.username}"
                        } else {
                            "🔑 Fill ${cred.serviceName}"
                        }
                        SuggestionItem(
                            displayText = label,
                            replacementText = cred.password,
                            isPrimary = true
                        )
                    }
                    suggestionsState.value = autofillChips
                } else {
                    suggestionsState.value = emptyList()
                }
                return@launch
            }

            if (!settings.suggestionsEnabled) {
                suggestionsState.value = emptyList()
                return@launch
            }

            val query = if (currentMode == KeyboardMode.AVRO && avroRawBuffer.isNotEmpty()) {
                avroRawBuffer.toString()
            } else {
                inputConnectionManager.getCurrentWordBeforeCursor()
            }

            val lang = when (currentMode) {
                KeyboardMode.AVRO -> "avro"
                KeyboardMode.BANGLA -> "bangla"
                else -> "english"
            }

            val list = suggestionEngine.getSuggestions(
                currentWord = query,
                mode = lang,
                isPasswordField = false
            )
            suggestionsState.value = list
        }
    }

    private fun learnWord(word: String, locale: String) {
        serviceScope.launch {
            val app = application as? NXVApplication ?: NXVApplication.instance
            app.repository.learnWord(word, locale)
        }
    }

    private fun syncSystemClipboard() {
        try {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
            if (cm.hasPrimaryClip() && (cm.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true ||
                        cm.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) == true)
            ) {
                val item = cm.primaryClip?.getItemAt(0)
                val text = item?.text?.toString() ?: item?.coerceToText(this)?.toString()
                if (!text.isNullOrBlank()) {
                    serviceScope.launch {
                        val app = application as? NXVApplication ?: NXVApplication.instance
                        app.repository.addClipboardItem(text)
                    }
                }
            }
        } catch (_: Exception) {}
    }
}
