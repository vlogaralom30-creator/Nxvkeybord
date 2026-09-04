package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ClipboardItem
import com.example.data.entity.SavedCredential
import com.example.data.preferences.KeyboardSettings
import com.example.ime.FeedbackManager
import com.example.keyboard.KeyboardMode
import com.example.keyboard.ShiftState
import com.example.language.bangla.BanglaLayouts
import com.example.language.english.EnglishEngine
import com.example.suggestion.SuggestionItem
import com.example.theme.KeyboardThemes

@Composable
fun KeyboardRootView(
    settings: KeyboardSettings,
    currentMode: KeyboardMode,
    shiftState: ShiftState,
    suggestions: List<SuggestionItem>,
    clipboardItems: List<ClipboardItem>,
    savedCredentials: List<SavedCredential> = emptyList(),
    autoSavePrompt: AutoSavePromptData? = null,
    enterLabel: String,
    feedbackManager: FeedbackManager,
    onCharTyped: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onSpaceDrag: ((Float) -> Unit)? = null,
    onEnter: () -> Unit,
    onShiftToggle: () -> Unit,
    onModeSwitch: (KeyboardMode) -> Unit,
    onLanguageCycle: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onSuggestionClicked: (SuggestionItem) -> Unit,
    onPasteClipboard: (String, Boolean) -> Unit,
    onTogglePinClipboard: (ClipboardItem) -> Unit,
    onDeleteClipboard: (Long) -> Unit,
    onClearClipboard: () -> Unit,
    onSyncClipboard: () -> Unit = {},
    onSaveCredential: (service: String, user: String, pass: String) -> Unit = { _, _, _ -> },
    onDeleteCredential: (Long) -> Unit = {},
    onTogglePinCredential: (SavedCredential) -> Unit = {},
    onOpenSettings: () -> Unit,
    onOpenThemes: (() -> Unit)? = null,
    onToggleVibration: ((Boolean) -> Unit)? = null,
    onToggleSound: ((Boolean) -> Unit)? = null,
    onUpdateOneHanded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = remember(settings.theme) {
        KeyboardThemes.getPalette(settings.theme)
    }

    val view = LocalView.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var isToolbarExpanded by remember { mutableStateOf(false) }

    fun playFeedback() {
        feedbackManager.playKeySound(settings.keySoundEnabled)
        feedbackManager.performHapticFeedback(view, settings.keyVibrationEnabled, settings.vibrationStrength)
    }

    val keyHeight = (44.dp * settings.keyboardHeightRatio).coerceIn(38.dp, 56.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.keyboardBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top border line for geometric precision
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(palette.dividerColor.copy(alpha = 0.5f))
            )

            // Suggestion Strip (shown unless in Emoji, Clipboard, or Vault mode)
            if (currentMode != KeyboardMode.EMOJI && currentMode != KeyboardMode.CLIPBOARD && currentMode != KeyboardMode.VAULT) {
                if (settings.suggestionsEnabled) {
                    SuggestionStrip(
                        suggestions = suggestions,
                        palette = palette,
                        currentLanguage = settings.currentLanguage,
                        autoSavePrompt = autoSavePrompt,
                        recentClip = clipboardItems.firstOrNull(),
                        clipboardCount = clipboardItems.size,
                        isToolbarExpanded = isToolbarExpanded,
                        keyVibrationEnabled = settings.keyVibrationEnabled,
                        keySoundEnabled = settings.keySoundEnabled,
                        oneHandedMode = settings.oneHandedMode,
                        onToggleToolbar = {
                            playFeedback()
                            isToolbarExpanded = !isToolbarExpanded
                        },
                        onSuggestionClick = {
                            playFeedback()
                            onSuggestionClicked(it)
                        },
                        onClipboardClick = {
                            playFeedback()
                            onSyncClipboard()
                            onModeSwitch(KeyboardMode.CLIPBOARD)
                        },
                        onQuickPaste = { text ->
                            playFeedback()
                            onPasteClipboard(text, true)
                        },
                        onVaultClick = {
                            playFeedback()
                            onModeSwitch(KeyboardMode.VAULT)
                        },
                        onThemesClick = {
                            playFeedback()
                            onOpenThemes?.invoke() ?: onOpenSettings()
                        },
                        onToggleVibration = {
                            val newVib = !settings.keyVibrationEnabled
                            onToggleVibration?.invoke(newVib)
                            if (newVib) {
                                feedbackManager.performHapticFeedback(view, true, settings.vibrationStrength)
                            }
                        },
                        onToggleSound = {
                            val newSound = !settings.keySoundEnabled
                            onToggleSound?.invoke(newSound)
                            if (newSound) {
                                feedbackManager.playKeySound(true)
                            }
                        },
                        onToggleOneHanded = {
                            val next = when (settings.oneHandedMode) {
                                "none" -> "right"
                                "right" -> "left"
                                else -> "none"
                            }
                            onUpdateOneHanded(next)
                        },
                        onSettingsClick = {
                            onOpenSettings()
                        },
                        onLanguageCycle = {
                            playFeedback()
                            onLanguageCycle()
                        },
                        onLanguageLongPress = {
                            playFeedback()
                            showLanguageDialog = true
                        }
                    )
                }
            }

            // One-handed wrapping
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left-side dock control if in right-handed mode
                if (settings.oneHandedMode == "right") {
                    OneHandedSideDock(
                        palette = palette,
                        onSwitchSide = { onUpdateOneHanded("left") },
                        onExpand = { onUpdateOneHanded("none") }
                    )
                }

                // Main Keyboard Body
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    when (currentMode) {
                        KeyboardMode.EMOJI -> {
                            EmojiKeyboardLayout(
                                palette = palette,
                                onEmojiSelected = { emoji ->
                                    playFeedback()
                                    onCharTyped(emoji)
                                },
                                onBackspace = {
                                    playFeedback()
                                    onDelete()
                                },
                                onCloseEmoji = {
                                    playFeedback()
                                    onModeSwitch(
                                        when (settings.currentLanguage) {
                                            "bangla" -> KeyboardMode.BANGLA
                                            "avro" -> KeyboardMode.AVRO
                                            else -> KeyboardMode.ENGLISH
                                        }
                                    )
                                }
                            )
                        }

                        KeyboardMode.CLIPBOARD -> {
                            ClipboardKeyboardLayout(
                                items = clipboardItems,
                                palette = palette,
                                onPasteItem = { text, returnToKeyboard ->
                                    playFeedback()
                                    onPasteClipboard(text, returnToKeyboard)
                                },
                                onTogglePin = { item ->
                                    playFeedback()
                                    onTogglePinClipboard(item)
                                },
                                onDeleteItem = { id ->
                                    playFeedback()
                                    onDeleteClipboard(id)
                                },
                                onClearAll = {
                                    playFeedback()
                                    onClearClipboard()
                                },
                                onSyncClipboard = {
                                    playFeedback()
                                    onSyncClipboard()
                                },
                                onCloseClipboard = {
                                    playFeedback()
                                    onModeSwitch(
                                        when (settings.currentLanguage) {
                                            "bangla" -> KeyboardMode.BANGLA
                                            "avro" -> KeyboardMode.AVRO
                                            else -> KeyboardMode.ENGLISH
                                        }
                                    )
                                }
                            )
                        }

                        KeyboardMode.VAULT -> {
                            CredentialVaultKeyboardLayout(
                                credentials = savedCredentials,
                                palette = palette,
                                onAutofillText = { text ->
                                    playFeedback()
                                    onPasteClipboard(text, true)
                                },
                                onSaveNewCredential = { service, user, pass ->
                                    playFeedback()
                                    onSaveCredential(service, user, pass)
                                },
                                onDeleteCredential = { id ->
                                    playFeedback()
                                    onDeleteCredential(id)
                                },
                                onTogglePin = { cred ->
                                    playFeedback()
                                    onTogglePinCredential(cred)
                                },
                                onCloseVault = {
                                    playFeedback()
                                    onModeSwitch(
                                        when (settings.currentLanguage) {
                                            "bangla" -> KeyboardMode.BANGLA
                                            "avro" -> KeyboardMode.AVRO
                                            else -> KeyboardMode.ENGLISH
                                        }
                                    )
                                }
                            )
                        }

                        KeyboardMode.NUMBERS -> {
                            NumberAndSymbolLayout(
                                isSymbolPage = false,
                                keyHeight = keyHeight,
                                palette = palette,
                                enterLabel = enterLabel,
                                showEmojiKey = settings.showEmojiKey,
                                showLanguageKey = settings.showLanguageKey,
                                showKeySubLabels = settings.showKeySubLabels,
                                popupMode = if (settings.keyPreviewEnabled) settings.keyPopupMode else "disabled",
                                onCharTyped = { char ->
                                    playFeedback()
                                    onCharTyped(char)
                                },
                                onDelete = {
                                    playFeedback()
                                    onDelete()
                                },
                                onSpace = {
                                    playFeedback()
                                    onSpace()
                                },
                                onSpaceDrag = onSpaceDrag,
                                onEnter = {
                                    playFeedback()
                                    onEnter()
                                },
                                onSwitchMode = { mode ->
                                    playFeedback()
                                    onModeSwitch(mode)
                                },
                                onTogglePage = {
                                    playFeedback()
                                    onModeSwitch(KeyboardMode.SYMBOLS)
                                },
                                onLanguageCycle = {
                                    playFeedback()
                                    onLanguageCycle()
                                },
                                onLongPressLanguage = {
                                    playFeedback()
                                    showLanguageDialog = true
                                }
                            )
                        }

                        KeyboardMode.SYMBOLS -> {
                            NumberAndSymbolLayout(
                                isSymbolPage = true,
                                keyHeight = keyHeight,
                                palette = palette,
                                enterLabel = enterLabel,
                                showEmojiKey = settings.showEmojiKey,
                                showLanguageKey = settings.showLanguageKey,
                                showKeySubLabels = settings.showKeySubLabels,
                                popupMode = if (settings.keyPreviewEnabled) settings.keyPopupMode else "disabled",
                                onCharTyped = { char ->
                                    playFeedback()
                                    onCharTyped(char)
                                },
                                onDelete = {
                                    playFeedback()
                                    onDelete()
                                },
                                onSpace = {
                                    playFeedback()
                                    onSpace()
                                },
                                onSpaceDrag = onSpaceDrag,
                                onEnter = {
                                    playFeedback()
                                    onEnter()
                                },
                                onSwitchMode = { mode ->
                                    playFeedback()
                                    onModeSwitch(mode)
                                },
                                onTogglePage = {
                                    playFeedback()
                                    onModeSwitch(KeyboardMode.NUMBERS)
                                },
                                onLanguageCycle = {
                                    playFeedback()
                                    onLanguageCycle()
                                },
                                onLongPressLanguage = {
                                    playFeedback()
                                    showLanguageDialog = true
                                }
                            )
                        }

                        KeyboardMode.BANGLA -> {
                            BanglaKeyLayout(
                                isShift = shiftState.isUppercase,
                                keyHeight = keyHeight,
                                palette = palette,
                                enterLabel = enterLabel,
                                showEmojiKey = settings.showEmojiKey,
                                showLanguageKey = settings.showLanguageKey,
                                showKeySubLabels = settings.showKeySubLabels,
                                popupMode = if (settings.keyPreviewEnabled) settings.keyPopupMode else "disabled",
                                onCharTyped = { char ->
                                    playFeedback()
                                    onCharTyped(char)
                                },
                                onDelete = {
                                    playFeedback()
                                    onDelete()
                                },
                                onSpace = {
                                    playFeedback()
                                    onSpace()
                                },
                                onSpaceDrag = onSpaceDrag,
                                onEnter = {
                                    playFeedback()
                                    onEnter()
                                },
                                onShift = {
                                    playFeedback()
                                    onShiftToggle()
                                },
                                onSwitchMode = { mode ->
                                    playFeedback()
                                    onModeSwitch(mode)
                                },
                                onLanguageCycle = {
                                    playFeedback()
                                    onLanguageCycle()
                                },
                                onLongPressLanguage = {
                                    playFeedback()
                                    showLanguageDialog = true
                                }
                            )
                        }

                        KeyboardMode.ENGLISH, KeyboardMode.AVRO -> {
                            // Both English and Avro use the QWERTY layout!
                            // (Avro converts English phonetics into Bangla)
                            val isAvro = currentMode == KeyboardMode.AVRO
                            QwertyKeyLayout(
                                isAvro = isAvro,
                                shiftState = shiftState,
                                showNumberRow = settings.showNumberRow,
                                keyHeight = keyHeight,
                                palette = palette,
                                enterLabel = enterLabel,
                                showEmojiKey = settings.showEmojiKey,
                                showLanguageKey = settings.showLanguageKey,
                                showKeySubLabels = settings.showKeySubLabels,
                                popupMode = if (settings.keyPreviewEnabled) settings.keyPopupMode else "disabled",
                                onCharTyped = { char ->
                                    playFeedback()
                                    val isLetter = char.length == 1 && char[0].isLetter()
                                    val finalChar = if (isLetter) {
                                        if (shiftState.isUppercase) char.uppercase() else char.lowercase()
                                    } else {
                                        char
                                    }
                                    onCharTyped(finalChar)
                                },
                                onDelete = {
                                    playFeedback()
                                    onDelete()
                                },
                                onSpace = {
                                    playFeedback()
                                    onSpace()
                                },
                                onSpaceDrag = onSpaceDrag,
                                onEnter = {
                                    playFeedback()
                                    onEnter()
                                },
                                onShift = {
                                    playFeedback()
                                    onShiftToggle()
                                },
                                onSwitchMode = { mode ->
                                    playFeedback()
                                    onModeSwitch(mode)
                                },
                                onLanguageCycle = {
                                    playFeedback()
                                    onLanguageCycle()
                                },
                                onLongPressLanguage = {
                                    playFeedback()
                                    showLanguageDialog = true
                                }
                            )
                        }
                    }
                }

                // Right-side dock control if in left-handed mode
                if (settings.oneHandedMode == "left") {
                    OneHandedSideDock(
                        palette = palette,
                        onSwitchSide = { onUpdateOneHanded("right") },
                        onExpand = { onUpdateOneHanded("none") }
                    )
                }
            }

            // Android gesture pill bar matching Geometric Balance layout
            Box(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 6.dp)
                    .width(80.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(palette.textColor.copy(alpha = 0.2f))
            )
        }

        // Language Selector Popup Dialog
        if (showLanguageDialog) {
            LanguageSelectorDialog(
                activeLanguages = settings.activeLanguages,
                currentLanguage = settings.currentLanguage,
                palette = palette,
                onSelectLanguage = { lang ->
                    onLanguageSelected(lang)
                },
                onDismiss = {
                    showLanguageDialog = false
                }
            )
        }
    }
}

@Composable
private fun OneHandedSideDock(
    palette: com.example.theme.KeyboardPalette,
    onSwitchSide: () -> Unit,
    onExpand: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(48.dp)
            .height(230.dp)
            .background(palette.keyActionBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSwitchSide() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⇆", color = palette.textColor, fontSize = 20.sp)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onExpand() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⛶", color = palette.textColor, fontSize = 18.sp)
        }
    }
}
