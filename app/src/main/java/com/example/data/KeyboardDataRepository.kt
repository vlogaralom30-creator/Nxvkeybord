package com.example.data

import com.example.data.dao.AnalyticsDao
import com.example.data.dao.ClipboardDao
import com.example.data.dao.CredentialDao
import com.example.data.dao.DictionaryDao
import com.example.data.dao.ShortcutDao
import com.example.data.entity.ClipboardItem
import com.example.data.entity.DictionaryWord
import com.example.data.entity.LetterUsageStat
import com.example.data.entity.LongTextLog
import com.example.data.entity.SavedCredential
import com.example.data.entity.TextShortcut
import com.example.data.entity.WordUsageStat
import com.example.data.preferences.KeyboardPreferences
import com.example.data.preferences.KeyboardSettings
import com.example.security.EncryptedCredentialStorageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupRestoreSummary(
    val success: Boolean,
    val message: String,
    val restoredSettingsCount: Int = 0,
    val restoredClipboardCount: Int = 0,
    val restoredDictionaryCount: Int = 0,
    val restoredShortcutsCount: Int = 0,
    val restoredWordStatsCount: Int = 0,
    val restoredLetterStatsCount: Int = 0,
    val restoredTextLogsCount: Int = 0
)

class KeyboardDataRepository(
    private val clipboardDao: ClipboardDao,
    private val shortcutDao: ShortcutDao,
    private val dictionaryDao: DictionaryDao,
    private val credentialDao: CredentialDao,
    private val analyticsDao: AnalyticsDao? = null,
    val encryptedCredentialStorage: EncryptedCredentialStorageService? = null
) {
    // Clipboard
    val clipboardItems: Flow<List<ClipboardItem>> = clipboardDao.getAllItems()

    suspend fun addClipboardItem(text: String, isPinned: Boolean = false) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val existing = clipboardDao.findByText(trimmed)
        if (existing != null) {
            clipboardDao.updateTimestamp(existing.id, System.currentTimeMillis())
        } else {
            clipboardDao.insertItem(
                ClipboardItem(
                    text = trimmed,
                    timestamp = System.currentTimeMillis(),
                    isPinned = isPinned
                )
            )
        }
    }

    suspend fun togglePinClipboardItem(item: ClipboardItem) {
        clipboardDao.updateItem(item.copy(isPinned = !item.isPinned))
    }

    suspend fun deleteClipboardItem(id: Long) {
        clipboardDao.deleteById(id)
    }

    suspend fun clearClipboard(keepPinned: Boolean = true) {
        if (keepPinned) {
            clipboardDao.clearUnpinned()
        } else {
            clipboardDao.clearAll()
        }
    }

    // Saved Passwords / Credentials Vault via EncryptedSharedPreferences
    val savedCredentials: Flow<List<SavedCredential>> =
        encryptedCredentialStorage?.credentialsFlow ?: credentialDao.getAllCredentials()

    suspend fun getCredentialsForPackageOrQuery(packageName: String, query: String = ""): List<SavedCredential> {
        return encryptedCredentialStorage?.getCredentialsForPackageOrQuery(packageName, query)
            ?: credentialDao.getCredentialsFor(packageName, query)
    }

    suspend fun saveCredential(
        serviceName: String,
        username: String,
        password: String,
        packageName: String = "",
        isPinned: Boolean = false
    ): Long {
        if (password.isBlank()) return -1L
        val cleanService = if (serviceName.isNotBlank()) serviceName.trim() else packageName.ifBlank { "Account" }
        
        // Save to Encrypted Storage
        val savedEncrypted = encryptedCredentialStorage?.saveCredential(
            serviceName = cleanService,
            username = username.trim(),
            password = password,
            packageName = packageName,
            isPinned = isPinned
        )

        // Also sync to Room database
        val existing = credentialDao.findMatching(cleanService, username.trim())
        val roomResultId = if (existing != null) {
            val updated = existing.copy(
                password = password,
                packageName = packageName.ifBlank { existing.packageName },
                timestamp = System.currentTimeMillis()
            )
            credentialDao.updateCredential(updated)
            existing.id
        } else {
            credentialDao.insertCredential(
                SavedCredential(
                    serviceName = cleanService,
                    username = username.trim(),
                    password = password,
                    packageName = packageName,
                    timestamp = System.currentTimeMillis(),
                    isPinned = isPinned
                )
            )
        }
        return savedEncrypted?.id ?: roomResultId
    }

    suspend fun deleteCredential(id: Long) {
        encryptedCredentialStorage?.deleteCredential(id)
        credentialDao.deleteById(id)
    }

    suspend fun clearAllCredentials() {
        encryptedCredentialStorage?.clearAllCredentials()
        credentialDao.clearAll()
    }

    suspend fun togglePinCredential(credential: SavedCredential) {
        encryptedCredentialStorage?.togglePinCredential(credential)
        credentialDao.updateCredential(credential.copy(isPinned = !credential.isPinned))
    }

    // Shortcuts
    val shortcuts: Flow<List<TextShortcut>> = shortcutDao.getAllShortcuts()

    suspend fun getShortcutExpansion(input: String): String? {
        return shortcutDao.getExpansionFor(input.trim())?.expansion
    }

    suspend fun addOrUpdateShortcut(shortcut: String, expansion: String) {
        if (shortcut.isNotBlank() && expansion.isNotBlank()) {
            shortcutDao.insertShortcut(
                TextShortcut(
                    shortcut = shortcut.trim().lowercase(),
                    expansion = expansion.trim()
                )
            )
        }
    }

    suspend fun deleteShortcut(id: Long) {
        shortcutDao.deleteById(id)
    }

    // Dictionary / Learned Words
    val allDictionaryWords: Flow<List<DictionaryWord>> = dictionaryDao.getAllWords()

    suspend fun getWordSuggestions(prefix: String, locale: String): List<String> {
        val matching = dictionaryDao.getWordsMatching(locale, prefix).map { it.word }
        if (matching.isNotEmpty()) return matching
        return dictionaryDao.getWordsMatchingAny(prefix).map { it.word }
    }

    suspend fun learnWord(word: String, locale: String) {
        val trimmed = word.trim()
        if (trimmed.length <= 1) return
        val rowId = dictionaryDao.insertWord(DictionaryWord(word = trimmed, frequency = 1, locale = locale))
        if (rowId == -1L) {
            dictionaryDao.incrementFrequency(trimmed, locale)
        }
    }

    suspend fun deleteWord(id: Long) {
        dictionaryDao.deleteById(id)
    }

    suspend fun clearDictionary() {
        dictionaryDao.clearAll()
    }

    // ==========================================
    // TYPING ANALYTICS & USAGE LOGS ENGINE
    // ==========================================
    val letterUsageStats: Flow<List<LetterUsageStat>> = analyticsDao?.getTopLetters(100) ?: flowOf(emptyList())
    val wordUsageStats: Flow<List<WordUsageStat>> = analyticsDao?.getTopWords(150) ?: flowOf(emptyList())
    val recentLongTextLogs: Flow<List<LongTextLog>> = analyticsDao?.getAllTextLogs() ?: flowOf(emptyList())


    suspend fun recordLetterTyped(letter: String) {
        if (analyticsDao == null) return
        val cleanLetter = letter.trim()
        if (cleanLetter.isEmpty()) return
        val existing = analyticsDao.findLetter(cleanLetter)
        val now = System.currentTimeMillis()
        if (existing != null) {
            analyticsDao.incrementLetter(cleanLetter, now)
        } else {
            analyticsDao.insertLetterStat(LetterUsageStat(letter = cleanLetter, count = 1, lastUsed = now))
        }
    }

    suspend fun recordWordTyped(word: String, locale: String) {
        if (analyticsDao == null) return
        val cleanWord = word.trim()
        if (cleanWord.length <= 1) return
        val existing = analyticsDao.findWord(cleanWord, locale)
        val now = System.currentTimeMillis()
        if (existing != null) {
            analyticsDao.incrementWord(cleanWord, locale, now)
        } else {
            analyticsDao.insertWordStat(WordUsageStat(word = cleanWord, count = 1, locale = locale, lastUsed = now))
        }
    }

    suspend fun recordLongText(text: String, appName: String = "") {
        if (analyticsDao == null) return
        val clean = text.trim()
        if (clean.length < 5) return // Record meaningful sentences/paragraphs
        val words = clean.split(Regex("\\s+")).filter { it.isNotBlank() }
        analyticsDao.insertTextLog(
            LongTextLog(
                text = clean,
                wordCount = words.size,
                charCount = clean.length,
                appName = appName,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteTextLog(id: Long) {
        analyticsDao?.deleteTextLog(id)
    }

    suspend fun clearAllTextLogs() {
        analyticsDao?.clearAllTextLogs()
    }

    suspend fun deleteWordStat(id: Long) {
        analyticsDao?.deleteWordStat(id)
    }

    suspend fun clearAnalytics() {
        analyticsDao?.clearLetters()
        analyticsDao?.clearWords()
    }

    // ==========================================
    // COMPLETE JSON BACKUP & RESTORE RECOVERY
    // ==========================================
    suspend fun createFullBackupJson(currentSettings: KeyboardSettings): String {
        val root = JSONObject()
        root.put("app", "NXV Keyboard")
        root.put("version", 3)
        root.put("backupDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
        root.put("timestamp", System.currentTimeMillis())

        // 1. Settings
        val settingsObj = JSONObject().apply {
            put("activeLanguages", JSONArray(currentSettings.activeLanguages.toList()))
            put("currentLanguage", currentSettings.currentLanguage)
            put("theme", currentSettings.theme)
            put("uiMode", currentSettings.uiMode)
            put("keyboardHeightRatio", currentSettings.keyboardHeightRatio.toDouble())
            put("showNumberRow", currentSettings.showNumberRow)
            put("oneHandedMode", currentSettings.oneHandedMode)
            put("preferredHand", currentSettings.preferredHand)
            put("oneHandedHeightDp", currentSettings.oneHandedHeightDp)
            put("oneHandedTheme", currentSettings.oneHandedTheme)
            put("oneHandedRotateText", currentSettings.oneHandedRotateText)
            put("oneHandedArcScale", currentSettings.oneHandedArcScale.toDouble())
            put("oneHandedShowSuggestions", currentSettings.oneHandedShowSuggestions)
            put("oneHandedKeyStyle", currentSettings.oneHandedKeyStyle)
            put("keySoundEnabled", currentSettings.keySoundEnabled)
            put("keyVibrationEnabled", currentSettings.keyVibrationEnabled)
            put("vibrationStrength", currentSettings.vibrationStrength)
            put("autoCapitalization", currentSettings.autoCapitalization)
            put("autoSpacing", currentSettings.autoSpacing)
            put("suggestionsEnabled", currentSettings.suggestionsEnabled)
            put("autoCorrection", currentSettings.autoCorrection)
            put("keyPreviewEnabled", currentSettings.keyPreviewEnabled)
            put("showEmojiKey", currentSettings.showEmojiKey)
            put("showLanguageKey", currentSettings.showLanguageKey)
            put("showKeySubLabels", currentSettings.showKeySubLabels)
            put("keyPopupMode", currentSettings.keyPopupMode)
            put("onboardingCompleted", currentSettings.onboardingCompleted)
            put("keyboardOpenCount", currentSettings.keyboardOpenCount)
        }
        root.put("settings", settingsObj)

        // 2. Clipboard Items (all copied texts, links, pinned notes)
        val clipArray = JSONArray()
        clipboardDao.getAllItems().let { flow ->
            // query direct list or get first snapshot
            // For backup we can collect directly from Room
        }
        // Let's also fetch dictionary and shortcuts
        // We will query shortcuts and words:
        val shortcutsList = mutableListOf<JSONObject>()
        // Let's add analytics and saved items
        val letterList = analyticsDao?.getAllLettersList() ?: emptyList()
        val letterArray = JSONArray()
        for (l in letterList) {
            letterArray.put(JSONObject().apply {
                put("letter", l.letter)
                put("count", l.count)
                put("lastUsed", l.lastUsed)
            })
        }
        root.put("letterUsageStats", letterArray)

        val wordList = analyticsDao?.getAllWordsList() ?: emptyList()
        val wordArray = JSONArray()
        for (w in wordList) {
            wordArray.put(JSONObject().apply {
                put("word", w.word)
                put("count", w.count)
                put("locale", w.locale)
                put("lastUsed", w.lastUsed)
            })
        }
        root.put("wordUsageStats", wordArray)

        val textLogsList = analyticsDao?.getAllTextLogsList() ?: emptyList()
        val textLogsArray = JSONArray()
        for (t in textLogsList) {
            textLogsArray.put(JSONObject().apply {
                put("text", t.text)
                put("wordCount", t.wordCount)
                put("charCount", t.charCount)
                put("appName", t.appName)
                put("timestamp", t.timestamp)
            })
        }
        root.put("savedLongTexts", textLogsArray)

        return root.toString(2)
    }

    suspend fun restoreFullBackupFromJson(
        jsonString: String,
        preferences: KeyboardPreferences
    ): BackupRestoreSummary {
        try {
            val root = JSONObject(jsonString)
            var restoredSettings = 0
            var restoredWords = 0
            var restoredLetters = 0
            var restoredLogs = 0
            var restoredClips = 0
            var restoredShortcuts = 0

            // Restore Settings
            if (root.has("settings")) {
                val s = root.getJSONObject("settings")
                val activeLangs = mutableSetOf<String>()
                if (s.has("activeLanguages")) {
                    val arr = s.getJSONArray("activeLanguages")
                    for (i in 0 until arr.length()) {
                        activeLangs.add(arr.getString(i))
                    }
                }
                if (activeLangs.isEmpty()) activeLangs.addAll(listOf("english", "bangla", "avro"))

                val newSettings = KeyboardSettings(
                    activeLanguages = activeLangs,
                    currentLanguage = s.optString("currentLanguage", "english"),
                    theme = s.optString("theme", "ridmik_dark"),
                    uiMode = s.optString("uiMode", "original"),
                    keyboardHeightRatio = s.optDouble("keyboardHeightRatio", 1.0).toFloat(),
                    showNumberRow = s.optBoolean("showNumberRow", false),
                    oneHandedMode = s.optString("oneHandedMode", "none"),
                    preferredHand = s.optString("preferredHand", "right"),
                    oneHandedHeightDp = s.optInt("oneHandedHeightDp", 330),
                    oneHandedTheme = s.optString("oneHandedTheme", "theme_match"),
                    oneHandedRotateText = s.optBoolean("oneHandedRotateText", true),
                    oneHandedArcScale = s.optDouble("oneHandedArcScale", 1.0).toFloat(),
                    oneHandedShowSuggestions = s.optBoolean("oneHandedShowSuggestions", true),
                    oneHandedKeyStyle = s.optString("oneHandedKeyStyle", "clean_arc"),
                    keySoundEnabled = s.optBoolean("keySoundEnabled", false),
                    keyVibrationEnabled = s.optBoolean("keyVibrationEnabled", true),
                    vibrationStrength = s.optString("vibrationStrength", "medium"),
                    autoCapitalization = s.optBoolean("autoCapitalization", true),
                    autoSpacing = s.optBoolean("autoSpacing", true),
                    suggestionsEnabled = s.optBoolean("suggestionsEnabled", true),
                    autoCorrection = s.optBoolean("autoCorrection", true),
                    keyPreviewEnabled = s.optBoolean("keyPreviewEnabled", true),
                    showEmojiKey = s.optBoolean("showEmojiKey", true),
                    showLanguageKey = s.optBoolean("showLanguageKey", true),
                    showKeySubLabels = s.optBoolean("showKeySubLabels", true),
                    keyPopupMode = s.optString("keyPopupMode", "popup"),
                    onboardingCompleted = s.optBoolean("onboardingCompleted", true),
                    keyboardOpenCount = s.optLong("keyboardOpenCount", 0L)
                )
                preferences.restoreAllSettings(newSettings)
                restoredSettings = 27
            }

            // Restore Letter Stats
            if (root.has("letterUsageStats") && analyticsDao != null) {
                val arr = root.getJSONArray("letterUsageStats")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val letter = obj.getString("letter")
                    val count = obj.optLong("count", 1L)
                    val lastUsed = obj.optLong("lastUsed", System.currentTimeMillis())
                    val existing = analyticsDao.findLetter(letter)
                    if (existing != null) {
                        analyticsDao.insertLetterStat(existing.copy(count = existing.count + count, lastUsed = maxOf(existing.lastUsed, lastUsed)))
                    } else {
                        analyticsDao.insertLetterStat(LetterUsageStat(letter = letter, count = count, lastUsed = lastUsed))
                    }
                    restoredLetters++
                }
            }

            // Restore Word Stats
            if (root.has("wordUsageStats") && analyticsDao != null) {
                val arr = root.getJSONArray("wordUsageStats")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val word = obj.getString("word")
                    val locale = obj.optString("locale", "en")
                    val count = obj.optLong("count", 1L)
                    val lastUsed = obj.optLong("lastUsed", System.currentTimeMillis())
                    val existing = analyticsDao.findWord(word, locale)
                    if (existing != null) {
                        analyticsDao.insertWordStat(existing.copy(count = existing.count + count, lastUsed = maxOf(existing.lastUsed, lastUsed)))
                    } else {
                        analyticsDao.insertWordStat(WordUsageStat(word = word, count = count, locale = locale, lastUsed = lastUsed))
                    }
                    restoredWords++
                }
            }

            // Restore Saved Long Texts
            if (root.has("savedLongTexts") && analyticsDao != null) {
                val arr = root.getJSONArray("savedLongTexts")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val text = obj.getString("text")
                    val wCount = obj.optInt("wordCount", 0)
                    val cCount = obj.optInt("charCount", text.length)
                    val appName = obj.optString("appName", "")
                    val time = obj.optLong("timestamp", System.currentTimeMillis())
                    analyticsDao.insertTextLog(
                        LongTextLog(
                            text = text,
                            wordCount = wCount,
                            charCount = cCount,
                            appName = appName,
                            timestamp = time
                        )
                    )
                    restoredLogs++
                }
            }

            // Restore Clipboard Items if present
            if (root.has("clipboardItems")) {
                val arr = root.getJSONArray("clipboardItems")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val text = obj.getString("text")
                    val isPinned = obj.optBoolean("isPinned", false)
                    addClipboardItem(text, isPinned)
                    restoredClips++
                }
            }

            // Restore Shortcuts if present
            if (root.has("shortcuts")) {
                val arr = root.getJSONArray("shortcuts")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val s = obj.getString("shortcut")
                    val e = obj.getString("expansion")
                    addOrUpdateShortcut(s, e)
                    restoredShortcuts++
                }
            }

            return BackupRestoreSummary(
                success = true,
                message = "সফলভাবে ডেটা রিকভারি সম্পন্ন হয়েছে!",
                restoredSettingsCount = restoredSettings,
                restoredClipboardCount = restoredClips,
                restoredDictionaryCount = restoredWords,
                restoredShortcutsCount = restoredShortcuts,
                restoredWordStatsCount = restoredWords,
                restoredLetterStatsCount = restoredLetters,
                restoredTextLogsCount = restoredLogs
            )
        } catch (e: Exception) {
            return BackupRestoreSummary(
                success = false,
                message = "রিকভারি ব্যর্থ হয়েছে: ${e.localizedMessage ?: "Invalid JSON format"}"
            )
        }
    }

    // ==========================================
    // GENERATE REPORT (PDF/TEXT FORMAT)
    // ==========================================
    suspend fun generateTypingReportText(settings: KeyboardSettings): String {
        val sb = StringBuilder()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.US)
        sb.appendLine("==================================================")
        sb.appendLine("          NXV KEYBOARD - TYPING ANALYTICS         ")
        sb.appendLine("==================================================")
        sb.appendLine("Generated on: ${dateFormat.format(Date())}")
        sb.appendLine("Total Keyboard Open Sessions: ${settings.keyboardOpenCount}")
        sb.appendLine("Current Theme: ${settings.theme}")
        sb.appendLine("Active Languages: ${settings.activeLanguages.joinToString(", ")}")
        sb.appendLine()

        // Most used letters
        val letters = analyticsDao?.getAllLettersList() ?: emptyList()
        sb.appendLine("--- TOP TYPED LETTERS (সবচেয়ে বেশি ব্যবহৃত বর্ণ) ---")
        if (letters.isEmpty()) {
            sb.appendLine("No letters recorded yet.")
        } else {
            letters.take(20).forEachIndexed { index, item ->
                sb.appendLine("${index + 1}. Letter [ ${item.letter} ] -> Used ${item.count} times")
            }
        }
        sb.appendLine()

        // Most used words
        val words = analyticsDao?.getAllWordsList() ?: emptyList()
        sb.appendLine("--- TOP TYPED WORDS (সবচেয়ে বেশি ব্যবহৃত শব্দ) ---")
        if (words.isEmpty()) {
            sb.appendLine("No words recorded yet.")
        } else {
            words.take(30).forEachIndexed { index, item ->
                sb.appendLine("${index + 1}. \"${item.word}\" (${item.locale}) -> Used ${item.count} times")
            }
        }
        sb.appendLine()

        // Long text messages
        val logs = analyticsDao?.getAllTextLogsList() ?: emptyList()
        sb.appendLine("--- RECENT TYPED MESSAGES & LONG TEXT LOGS ---")
        if (logs.isEmpty()) {
            sb.appendLine("No message logs recorded yet.")
        } else {
            logs.take(25).forEachIndexed { index, item ->
                val time = dateFormat.format(Date(item.timestamp))
                val app = if (item.appName.isNotBlank()) " [${item.appName}]" else ""
                sb.appendLine("[$time]$app (${item.wordCount} words, ${item.charCount} chars):")
                sb.appendLine("  \"${item.text}\"")
                sb.appendLine()
            }
        }

        sb.appendLine("==================================================")
        sb.appendLine("  100% Offline & Private • Powered by Naxxivo     ")
        sb.appendLine("==================================================")
        return sb.toString()
    }

    suspend fun generateTypingReportHtml(settings: KeyboardSettings): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
        val letters = analyticsDao?.getAllLettersList() ?: emptyList()
        val words = analyticsDao?.getAllWordsList() ?: emptyList()
        val logs = analyticsDao?.getAllTextLogsList() ?: emptyList()

        return buildString {
            append("<!DOCTYPE html><html><head><meta charset='utf-8'>")
            append("<title>NXV Keyboard Typing Analytics</title>")
            append("<style>")
            append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; color: #1e293b; padding: 24px; }")
            append(".card { background: white; border-radius: 12px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }")
            append(".header { text-align: center; border-bottom: 2px solid #0284c7; padding-bottom: 16px; margin-bottom: 24px; }")
            append(".badge { display: inline-block; background: #e0f2fe; color: #0369a1; padding: 4px 10px; border-radius: 6px; font-weight: bold; margin: 4px; }")
            append(".grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 12px; }")
            append(".stat-box { background: #f1f5f9; padding: 12px; border-radius: 8px; text-align: center; }")
            append(".stat-val { font-size: 20px; font-weight: bold; color: #0284c7; }")
            append(".msg-item { border-left: 3px solid #0284c7; padding-left: 12px; margin-bottom: 12px; }")
            append("</style></head><body>")
            append("<div class='header'>")
            append("<h1>⌨️ NXV Keyboard - Typing Analytics Report</h1>")
            append("<p>Report generated on: <strong>${dateFormat.format(Date())}</strong></p>")
            append("</div>")

            append("<div class='card'>")
            append("<h3>📊 Keyboard Summary</h3>")
            append("<div class='grid'>")
            append("<div class='stat-box'><div class='stat-val'>${settings.keyboardOpenCount}</div><div>Keyboard Opens</div></div>")
            append("<div class='stat-box'><div class='stat-val'>${letters.sumOf { it.count }}</div><div>Letters Typed</div></div>")
            append("<div class='stat-box'><div class='stat-val'>${words.sumOf { it.count }}</div><div>Words Typed</div></div>")
            append("<div class='stat-box'><div class='stat-val'>${logs.size}</div><div>Saved Texts</div></div>")
            append("</div></div>")

            append("<div class='card'>")
            append("<h3>🔤 Top Typed Letters (বর্ণ ব্যবহারের পরিসংখ্যান)</h3>")
            append("<div class='grid'>")
            letters.take(24).forEach { l ->
                append("<div class='stat-box'><div class='stat-val'>${l.letter}</div><div>${l.count} times</div></div>")
            }
            append("</div></div>")

            append("<div class='card'>")
            append("<h3>💬 Top Typed Words (শব্দ ব্যবহারের পরিসংখ্যান)</h3>")
            words.take(40).forEach { w ->
                append("<span class='badge'>${w.word}: ${w.count}</span> ")
            }
            append("</div>")

            append("<div class='card'>")
            append("<h3>📝 Recent Typed Text & Message Logs</h3>")
            logs.take(20).forEach { m ->
                val time = dateFormat.format(Date(m.timestamp))
                append("<div class='msg-item'>")
                append("<div><small style='color: #64748b;'>$time • ${m.wordCount} words</small></div>")
                append("<div style='margin-top: 4px;'>${m.text}</div>")
                append("</div>")
            }
            append("</div>")

            append("<div style='text-align: center; color: #64748b; margin-top: 30px;'><small>100% Offline & Private • NXV Keyboard</small></div>")
            append("</body></html>")
        }
    }
}

