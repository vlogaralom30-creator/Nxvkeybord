package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nxv_keyboard_prefs")

data class KeyboardSettings(
    val activeLanguages: Set<String> = setOf("english", "bangla", "avro"),
    val currentLanguage: String = "english",
    val theme: String = "dark",
    val uiMode: String = "original",
    val keyboardHeightRatio: Float = 1.0f,
    val showNumberRow: Boolean = false,
    val oneHandedMode: String = "none",
    val preferredHand: String = "right",
    val oneHandedHeightDp: Int = 330,
    val oneHandedTheme: String = "theme_match",
    val oneHandedRotateText: Boolean = true,
    val oneHandedArcScale: Float = 1.0f,
    val oneHandedShowSuggestions: Boolean = true,
    val oneHandedKeyStyle: String = "clean_arc",
    val keySoundEnabled: Boolean = false,
    val keyVibrationEnabled: Boolean = true,
    val vibrationStrength: String = "medium",
    val autoCapitalization: Boolean = true,
    val autoSpacing: Boolean = true,
    val suggestionsEnabled: Boolean = true,
    val autoCorrection: Boolean = true,
    val keyPreviewEnabled: Boolean = true,
    val showEmojiKey: Boolean = true,
    val showLanguageKey: Boolean = true,
    val showKeySubLabels: Boolean = true,
    val keyPopupMode: String = "popup",
    val onboardingCompleted: Boolean = false,
    val keyboardOpenCount: Long = 0L,

    // Deep Theme & Color Customization
    val customThemeEnabled: Boolean = false,
    val customKeyboardBgColor: Long = 0xFF121316L,
    val customKeyBgColor: Long = 0xFF20232AL,
    val customKeyActionBgColor: Long = 0xFF181A1FL,
    val customTextColor: Long = 0xFFF0F4F8L,
    val customSecondaryTextColor: Long = 0xFF7C889BL,
    val customAccentColor: Long = 0xFF00D2FFL,
    val customSuggestionBgColor: Long = 0xFF16181DL,
    val customBackgroundImageUri: String = "",
    val customBackgroundDim: Float = 0.40f,
    val customBackgroundBlur: Float = 0f,
    val customThemeIconStyle: String = "standard",
    val customKeyBorderWidthDp: Float = 0.5f,
    val customKeyBorderColor: Long = 0x2AFFFFFFL,
    val keyElevationDp: Float = 1.5f,

    // Keyboard Sizing & Scaling (Side size% & Up-to-down size% setup)
    val keyboardWidthRatio: Float = 1.0f,
    val keyCornerRadiusDp: Int = 8,
    val keyVerticalGapDp: Int = 5,
    val keyHorizontalGapDp: Int = 4,
    val keyboardSideMarginDp: Int = 0,
    val keyboardBottomMarginDp: Int = 0,
    val keyFontSizeRatio: Float = 1.0f,

    // Custom Shortcut Buttons Setup (Under/in Suggestion Bar)
    val enabledShortcuts: Set<String> = setOf(
        "clipboard", "theme", "edittext", "numpad", "mic", "video_overlay",
        "stickers", "media", "vault", "language", "vibration", "sound", "one_handed", "settings"
    ),
    val shortcutOrder: String = "clipboard,theme,edittext,numpad,mic,video_overlay,stickers,media,vault,language,vibration,sound,one_handed,settings",
    val showSuggestionMicIcon: Boolean = true,
    val showSuggestionVideoIcon: Boolean = true,
    val showQuickPasteChip: Boolean = true,
    val toolbarPosition: String = "below_suggestions"
)

class KeyboardPreferences(private val context: Context) {

    companion object {
        val KEY_ACTIVE_LANGUAGES = stringSetPreferencesKey("active_languages")
        val KEY_CURRENT_LANGUAGE = stringPreferencesKey("current_language")
        val KEY_THEME = stringPreferencesKey("theme")
        val KEY_UI_MODE = stringPreferencesKey("selected_ui_mode")
        val KEY_HEIGHT_RATIO = floatPreferencesKey("keyboard_height_ratio")
        val KEY_SHOW_NUMBER_ROW = booleanPreferencesKey("show_number_row")
        val KEY_ONE_HANDED_MODE = stringPreferencesKey("one_handed_mode")
        val KEY_PREFERRED_HAND = stringPreferencesKey("preferred_hand")
        val KEY_ONE_HANDED_HEIGHT = floatPreferencesKey("one_handed_height_dp")
        val KEY_ONE_HANDED_THEME = stringPreferencesKey("one_handed_theme")
        val KEY_ONE_HANDED_ROTATE_TEXT = booleanPreferencesKey("one_handed_rotate_text")
        val KEY_ONE_HANDED_ARC_SCALE = floatPreferencesKey("one_handed_arc_scale")
        val KEY_ONE_HANDED_SHOW_SUGG = booleanPreferencesKey("one_handed_show_sugg")
        val KEY_ONE_HANDED_KEY_STYLE = stringPreferencesKey("one_handed_key_style")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("key_sound_enabled")
        val KEY_VIBRATION_ENABLED = booleanPreferencesKey("key_vibration_enabled")
        val KEY_VIBRATION_STRENGTH = stringPreferencesKey("vibration_strength")
        val KEY_AUTO_CAPITALIZATION = booleanPreferencesKey("auto_capitalization")
        val KEY_AUTO_SPACING = booleanPreferencesKey("auto_spacing")
        val KEY_SUGGESTIONS_ENABLED = booleanPreferencesKey("suggestions_enabled")
        val KEY_AUTO_CORRECTION = booleanPreferencesKey("auto_correction")
        val KEY_KEY_PREVIEW = booleanPreferencesKey("key_preview_enabled")
        val KEY_SHOW_EMOJI_KEY = booleanPreferencesKey("show_emoji_key")
        val KEY_SHOW_LANGUAGE_KEY = booleanPreferencesKey("show_language_key")
        val KEY_SHOW_KEY_SUBLABELS = booleanPreferencesKey("show_key_sublabels")
        val KEY_KEY_POPUP_MODE = stringPreferencesKey("key_popup_mode")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_KEYBOARD_OPEN_COUNT = androidx.datastore.preferences.core.longPreferencesKey("keyboard_open_count")

        // Deep Customization Keys
        val KEY_CUSTOM_THEME_ENABLED = booleanPreferencesKey("custom_theme_enabled")
        val KEY_CUSTOM_KEYBOARD_BG = androidx.datastore.preferences.core.longPreferencesKey("custom_keyboard_bg_color")
        val KEY_CUSTOM_KEY_BG = androidx.datastore.preferences.core.longPreferencesKey("custom_key_bg_color")
        val KEY_CUSTOM_KEY_ACTION_BG = androidx.datastore.preferences.core.longPreferencesKey("custom_key_action_bg_color")
        val KEY_CUSTOM_TEXT_COLOR = androidx.datastore.preferences.core.longPreferencesKey("custom_text_color")
        val KEY_CUSTOM_SECONDARY_TEXT_COLOR = androidx.datastore.preferences.core.longPreferencesKey("custom_secondary_text_color")
        val KEY_CUSTOM_ACCENT_COLOR = androidx.datastore.preferences.core.longPreferencesKey("custom_accent_color")
        val KEY_CUSTOM_SUGG_BG = androidx.datastore.preferences.core.longPreferencesKey("custom_sugg_bg_color")
        val KEY_CUSTOM_BG_IMAGE_URI = stringPreferencesKey("custom_bg_image_uri")
        val KEY_CUSTOM_BG_DIM = floatPreferencesKey("custom_bg_dim")
        val KEY_CUSTOM_BG_BLUR = floatPreferencesKey("custom_bg_blur")
        val KEY_CUSTOM_ICON_STYLE = stringPreferencesKey("custom_icon_style")
        val KEY_CUSTOM_BORDER_WIDTH = floatPreferencesKey("custom_border_width_dp")
        val KEY_CUSTOM_BORDER_COLOR = androidx.datastore.preferences.core.longPreferencesKey("custom_border_color")
        val KEY_KEY_ELEVATION = floatPreferencesKey("key_elevation_dp")

        val KEY_WIDTH_RATIO = floatPreferencesKey("keyboard_width_ratio")
        val KEY_KEY_CORNER_RADIUS = androidx.datastore.preferences.core.intPreferencesKey("key_corner_radius_dp")
        val KEY_KEY_VERTICAL_GAP = androidx.datastore.preferences.core.intPreferencesKey("key_vertical_gap_dp")
        val KEY_KEY_HORIZONTAL_GAP = androidx.datastore.preferences.core.intPreferencesKey("key_horizontal_gap_dp")
        val KEY_SIDE_MARGIN = androidx.datastore.preferences.core.intPreferencesKey("keyboard_side_margin_dp")
        val KEY_BOTTOM_MARGIN = androidx.datastore.preferences.core.intPreferencesKey("keyboard_bottom_margin_dp")
        val KEY_KEY_FONT_SIZE_RATIO = floatPreferencesKey("key_font_size_ratio")

        val KEY_ENABLED_SHORTCUTS = stringSetPreferencesKey("enabled_shortcuts")
        val KEY_SHORTCUT_ORDER = stringPreferencesKey("shortcut_order")
        val KEY_SHOW_SUGG_MIC = booleanPreferencesKey("show_sugg_mic")
        val KEY_SHOW_SUGG_VIDEO = booleanPreferencesKey("show_sugg_video")
        val KEY_SHOW_QUICK_PASTE = booleanPreferencesKey("show_quick_paste")
        val KEY_TOOLBAR_POSITION = stringPreferencesKey("toolbar_position")
    }

    val settingsFlow: Flow<KeyboardSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                android.util.Log.e("KeyboardPreferences", "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
        KeyboardSettings(
            activeLanguages = prefs[KEY_ACTIVE_LANGUAGES] ?: setOf("english", "bangla", "avro"),
            currentLanguage = prefs[KEY_CURRENT_LANGUAGE] ?: "english",
            theme = prefs[KEY_THEME] ?: "dark",
            uiMode = prefs[KEY_UI_MODE] ?: "original",
            keyboardHeightRatio = prefs[KEY_HEIGHT_RATIO] ?: 1.0f,
            showNumberRow = prefs[KEY_SHOW_NUMBER_ROW] ?: false,
            oneHandedMode = prefs[KEY_ONE_HANDED_MODE] ?: "none",
            preferredHand = prefs[KEY_PREFERRED_HAND] ?: "right",
            oneHandedHeightDp = (prefs[KEY_ONE_HANDED_HEIGHT] ?: 330f).toInt(),
            oneHandedTheme = prefs[KEY_ONE_HANDED_THEME] ?: "theme_match",
            oneHandedRotateText = prefs[KEY_ONE_HANDED_ROTATE_TEXT] ?: true,
            oneHandedArcScale = prefs[KEY_ONE_HANDED_ARC_SCALE] ?: 1.0f,
            oneHandedShowSuggestions = prefs[KEY_ONE_HANDED_SHOW_SUGG] ?: true,
            oneHandedKeyStyle = prefs[KEY_ONE_HANDED_KEY_STYLE] ?: "clean_arc",
            keySoundEnabled = prefs[KEY_SOUND_ENABLED] ?: false,
            keyVibrationEnabled = prefs[KEY_VIBRATION_ENABLED] ?: true,
            vibrationStrength = prefs[KEY_VIBRATION_STRENGTH] ?: "medium",
            autoCapitalization = prefs[KEY_AUTO_CAPITALIZATION] ?: true,
            autoSpacing = prefs[KEY_AUTO_SPACING] ?: true,
            suggestionsEnabled = prefs[KEY_SUGGESTIONS_ENABLED] ?: true,
            autoCorrection = prefs[KEY_AUTO_CORRECTION] ?: true,
            keyPreviewEnabled = prefs[KEY_KEY_PREVIEW] ?: true,
            showEmojiKey = prefs[KEY_SHOW_EMOJI_KEY] ?: true,
            showLanguageKey = prefs[KEY_SHOW_LANGUAGE_KEY] ?: true,
            showKeySubLabels = prefs[KEY_SHOW_KEY_SUBLABELS] ?: true,
            keyPopupMode = prefs[KEY_KEY_POPUP_MODE] ?: "popup",
            onboardingCompleted = prefs[KEY_ONBOARDING_COMPLETED] ?: false,
            keyboardOpenCount = prefs[KEY_KEYBOARD_OPEN_COUNT] ?: 0L,

            // Deep customization
            customThemeEnabled = prefs[KEY_CUSTOM_THEME_ENABLED] ?: false,
            customKeyboardBgColor = prefs[KEY_CUSTOM_KEYBOARD_BG] ?: 0xFF121316L,
            customKeyBgColor = prefs[KEY_CUSTOM_KEY_BG] ?: 0xFF20232AL,
            customKeyActionBgColor = prefs[KEY_CUSTOM_KEY_ACTION_BG] ?: 0xFF181A1FL,
            customTextColor = prefs[KEY_CUSTOM_TEXT_COLOR] ?: 0xFFF0F4F8L,
            customSecondaryTextColor = prefs[KEY_CUSTOM_SECONDARY_TEXT_COLOR] ?: 0xFF7C889BL,
            customAccentColor = prefs[KEY_CUSTOM_ACCENT_COLOR] ?: 0xFF00D2FFL,
            customSuggestionBgColor = prefs[KEY_CUSTOM_SUGG_BG] ?: 0xFF16181DL,
            customBackgroundImageUri = prefs[KEY_CUSTOM_BG_IMAGE_URI] ?: "",
            customBackgroundDim = prefs[KEY_CUSTOM_BG_DIM] ?: 0.40f,
            customBackgroundBlur = prefs[KEY_CUSTOM_BG_BLUR] ?: 0f,
            customThemeIconStyle = prefs[KEY_CUSTOM_ICON_STYLE] ?: "standard",
            customKeyBorderWidthDp = prefs[KEY_CUSTOM_BORDER_WIDTH] ?: 0.5f,
            customKeyBorderColor = prefs[KEY_CUSTOM_BORDER_COLOR] ?: 0x2AFFFFFFL,
            keyElevationDp = prefs[KEY_KEY_ELEVATION] ?: 1.5f,

            keyboardWidthRatio = prefs[KEY_WIDTH_RATIO] ?: 1.0f,
            keyCornerRadiusDp = prefs[KEY_KEY_CORNER_RADIUS] ?: 8,
            keyVerticalGapDp = prefs[KEY_KEY_VERTICAL_GAP] ?: 5,
            keyHorizontalGapDp = prefs[KEY_KEY_HORIZONTAL_GAP] ?: 4,
            keyboardSideMarginDp = prefs[KEY_SIDE_MARGIN] ?: 0,
            keyboardBottomMarginDp = prefs[KEY_BOTTOM_MARGIN] ?: 0,
            keyFontSizeRatio = prefs[KEY_KEY_FONT_SIZE_RATIO] ?: 1.0f,

            enabledShortcuts = prefs[KEY_ENABLED_SHORTCUTS] ?: setOf(
                "clipboard", "theme", "edittext", "numpad", "mic", "video_overlay",
                "stickers", "media", "vault", "language", "vibration", "sound", "one_handed", "settings"
            ),
            shortcutOrder = prefs[KEY_SHORTCUT_ORDER] ?: "clipboard,theme,edittext,numpad,mic,video_overlay,stickers,media,vault,language,vibration,sound,one_handed,settings",
            showSuggestionMicIcon = prefs[KEY_SHOW_SUGG_MIC] ?: true,
            showSuggestionVideoIcon = prefs[KEY_SHOW_SUGG_VIDEO] ?: true,
            showQuickPasteChip = prefs[KEY_SHOW_QUICK_PASTE] ?: true,
            toolbarPosition = prefs[KEY_TOOLBAR_POSITION] ?: "below_suggestions"
        )
    }

    suspend fun updateActiveLanguages(languages: Set<String>) {
        context.dataStore.edit { it[KEY_ACTIVE_LANGUAGES] = languages }
    }

    suspend fun updateCurrentLanguage(language: String) {
        context.dataStore.edit { it[KEY_CURRENT_LANGUAGE] = language }
    }

    suspend fun updateTheme(theme: String) {
        context.dataStore.edit { it[KEY_THEME] = theme }
    }

    suspend fun updateUiMode(mode: String) {
        context.dataStore.edit { it[KEY_UI_MODE] = mode }
    }

    suspend fun updateKeyboardHeightRatio(ratio: Float) {
        context.dataStore.edit { it[KEY_HEIGHT_RATIO] = ratio.coerceIn(0.80f, 1.30f) }
    }

    suspend fun updateShowNumberRow(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_NUMBER_ROW] = show }
    }

    suspend fun updateOneHandedMode(mode: String) {
        context.dataStore.edit { it[KEY_ONE_HANDED_MODE] = mode }
    }

    suspend fun updatePreferredHand(hand: String) {
        context.dataStore.edit { it[KEY_PREFERRED_HAND] = hand }
    }

    suspend fun updateOneHandedHeightDp(heightDp: Int) {
        context.dataStore.edit { it[KEY_ONE_HANDED_HEIGHT] = heightDp.toFloat() }
    }

    suspend fun updateOneHandedTheme(theme: String) {
        context.dataStore.edit { it[KEY_ONE_HANDED_THEME] = theme }
    }

    suspend fun updateOneHandedRotateText(rotate: Boolean) {
        context.dataStore.edit { it[KEY_ONE_HANDED_ROTATE_TEXT] = rotate }
    }

    suspend fun updateOneHandedArcScale(scale: Float) {
        context.dataStore.edit { it[KEY_ONE_HANDED_ARC_SCALE] = scale }
    }

    suspend fun updateOneHandedShowSuggestions(show: Boolean) {
        context.dataStore.edit { it[KEY_ONE_HANDED_SHOW_SUGG] = show }
    }

    suspend fun updateOneHandedKeyStyle(style: String) {
        context.dataStore.edit { it[KEY_ONE_HANDED_KEY_STYLE] = style }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION_ENABLED] = enabled }
    }

    suspend fun updateVibrationStrength(strength: String) {
        context.dataStore.edit { it[KEY_VIBRATION_STRENGTH] = strength }
    }

    suspend fun updateAutoCapitalization(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_CAPITALIZATION] = enabled }
    }

    suspend fun updateAutoSpacing(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_SPACING] = enabled }
    }

    suspend fun updateSuggestionsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SUGGESTIONS_ENABLED] = enabled }
    }

    suspend fun updateAutoCorrection(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_CORRECTION] = enabled }
    }

    suspend fun updateKeyPreviewEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_KEY_PREVIEW] = enabled }
    }

    suspend fun updateShowEmojiKey(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_EMOJI_KEY] = show }
    }

    suspend fun updateShowLanguageKey(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_LANGUAGE_KEY] = show }
    }

    suspend fun updateShowKeySubLabels(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_KEY_SUBLABELS] = show }
    }

    suspend fun updateKeyPopupMode(mode: String) {
        context.dataStore.edit { it[KEY_KEY_POPUP_MODE] = mode }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun incrementKeyboardOpenCount() {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_KEYBOARD_OPEN_COUNT] ?: 0L
            prefs[KEY_KEYBOARD_OPEN_COUNT] = current + 1L
        }
    }

    suspend fun resetKeyboardOpenCount() {
        context.dataStore.edit { it[KEY_KEYBOARD_OPEN_COUNT] = 0L }
    }

    suspend fun updateCustomThemeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_CUSTOM_THEME_ENABLED] = enabled }
    }

    suspend fun updateCustomKeyboardBg(color: Long) {
        context.dataStore.edit { it[KEY_CUSTOM_KEYBOARD_BG] = color }
    }

    suspend fun updateCustomKeyBg(color: Long) {
        context.dataStore.edit { it[KEY_CUSTOM_KEY_BG] = color }
    }

    suspend fun updateCustomKeyActionBg(color: Long) {
        context.dataStore.edit { it[KEY_CUSTOM_KEY_ACTION_BG] = color }
    }

    suspend fun updateCustomTextColor(color: Long) {
        context.dataStore.edit { it[KEY_CUSTOM_TEXT_COLOR] = color }
    }

    suspend fun updateCustomSecondaryTextColor(color: Long) {
        context.dataStore.edit { it[KEY_CUSTOM_SECONDARY_TEXT_COLOR] = color }
    }

    suspend fun updateCustomAccentColor(color: Long) {
        context.dataStore.edit { it[KEY_CUSTOM_ACCENT_COLOR] = color }
    }

    suspend fun updateCustomSuggestionBg(color: Long) {
        context.dataStore.edit { it[KEY_CUSTOM_SUGG_BG] = color }
    }

    suspend fun updateCustomBackgroundImageUri(uri: String) {
        context.dataStore.edit { it[KEY_CUSTOM_BG_IMAGE_URI] = uri }
    }

    suspend fun updateCustomBackgroundDim(dim: Float) {
        context.dataStore.edit { it[KEY_CUSTOM_BG_DIM] = dim.coerceIn(0f, 1f) }
    }

    suspend fun updateCustomBackgroundBlur(blur: Float) {
        context.dataStore.edit { it[KEY_CUSTOM_BG_BLUR] = blur.coerceIn(0f, 25f) }
    }

    suspend fun updateCustomIconStyle(style: String) {
        context.dataStore.edit { it[KEY_CUSTOM_ICON_STYLE] = style }
    }

    suspend fun updateCustomKeyBorderWidthDp(width: Float) {
        context.dataStore.edit { it[KEY_CUSTOM_BORDER_WIDTH] = width.coerceIn(0f, 4f) }
    }

    suspend fun updateCustomKeyBorderColor(color: Long) {
        context.dataStore.edit { it[KEY_CUSTOM_BORDER_COLOR] = color }
    }

    suspend fun updateKeyElevationDp(elevation: Float) {
        context.dataStore.edit { it[KEY_KEY_ELEVATION] = elevation.coerceIn(0f, 8f) }
    }

    suspend fun updateKeyboardWidthRatio(ratio: Float) {
        context.dataStore.edit { it[KEY_WIDTH_RATIO] = ratio.coerceIn(0.70f, 1.0f) }
    }

    suspend fun updateKeyCornerRadiusDp(radiusDp: Int) {
        context.dataStore.edit { it[KEY_KEY_CORNER_RADIUS] = radiusDp.coerceIn(0, 24) }
    }

    suspend fun updateKeyVerticalGapDp(gapDp: Int) {
        context.dataStore.edit { it[KEY_KEY_VERTICAL_GAP] = gapDp.coerceIn(1, 16) }
    }

    suspend fun updateKeyHorizontalGapDp(gapDp: Int) {
        context.dataStore.edit { it[KEY_KEY_HORIZONTAL_GAP] = gapDp.coerceIn(1, 14) }
    }

    suspend fun updateKeyboardSideMarginDp(marginDp: Int) {
        context.dataStore.edit { it[KEY_SIDE_MARGIN] = marginDp.coerceIn(0, 32) }
    }

    suspend fun updateKeyboardBottomMarginDp(marginDp: Int) {
        context.dataStore.edit { it[KEY_BOTTOM_MARGIN] = marginDp.coerceIn(0, 32) }
    }

    suspend fun updateKeyFontSizeRatio(ratio: Float) {
        context.dataStore.edit { it[KEY_KEY_FONT_SIZE_RATIO] = ratio.coerceIn(0.75f, 1.35f) }
    }

    suspend fun updateEnabledShortcuts(shortcuts: Set<String>) {
        context.dataStore.edit { it[KEY_ENABLED_SHORTCUTS] = shortcuts }
    }

    suspend fun updateShortcutOrder(order: String) {
        context.dataStore.edit { it[KEY_SHORTCUT_ORDER] = order }
    }

    suspend fun updateShowSuggestionMic(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_SUGG_MIC] = show }
    }

    suspend fun updateShowSuggestionVideo(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_SUGG_VIDEO] = show }
    }

    suspend fun updateShowQuickPaste(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_QUICK_PASTE] = show }
    }

    suspend fun updateToolbarPosition(position: String) {
        context.dataStore.edit { it[KEY_TOOLBAR_POSITION] = position }
    }

    suspend fun restoreAllSettings(settings: KeyboardSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVE_LANGUAGES] = settings.activeLanguages
            prefs[KEY_CURRENT_LANGUAGE] = settings.currentLanguage
            prefs[KEY_THEME] = settings.theme
            prefs[KEY_UI_MODE] = settings.uiMode
            prefs[KEY_HEIGHT_RATIO] = settings.keyboardHeightRatio
            prefs[KEY_SHOW_NUMBER_ROW] = settings.showNumberRow
            prefs[KEY_ONE_HANDED_MODE] = settings.oneHandedMode
            prefs[KEY_PREFERRED_HAND] = settings.preferredHand
            prefs[KEY_ONE_HANDED_HEIGHT] = settings.oneHandedHeightDp.toFloat()
            prefs[KEY_ONE_HANDED_THEME] = settings.oneHandedTheme
            prefs[KEY_ONE_HANDED_ROTATE_TEXT] = settings.oneHandedRotateText
            prefs[KEY_ONE_HANDED_ARC_SCALE] = settings.oneHandedArcScale
            prefs[KEY_ONE_HANDED_SHOW_SUGG] = settings.oneHandedShowSuggestions
            prefs[KEY_ONE_HANDED_KEY_STYLE] = settings.oneHandedKeyStyle
            prefs[KEY_SOUND_ENABLED] = settings.keySoundEnabled
            prefs[KEY_VIBRATION_ENABLED] = settings.keyVibrationEnabled
            prefs[KEY_VIBRATION_STRENGTH] = settings.vibrationStrength
            prefs[KEY_AUTO_CAPITALIZATION] = settings.autoCapitalization
            prefs[KEY_AUTO_SPACING] = settings.autoSpacing
            prefs[KEY_SUGGESTIONS_ENABLED] = settings.suggestionsEnabled
            prefs[KEY_AUTO_CORRECTION] = settings.autoCorrection
            prefs[KEY_KEY_PREVIEW] = settings.keyPreviewEnabled
            prefs[KEY_SHOW_EMOJI_KEY] = settings.showEmojiKey
            prefs[KEY_SHOW_LANGUAGE_KEY] = settings.showLanguageKey
            prefs[KEY_SHOW_KEY_SUBLABELS] = settings.showKeySubLabels
            prefs[KEY_KEY_POPUP_MODE] = settings.keyPopupMode
            prefs[KEY_ONBOARDING_COMPLETED] = settings.onboardingCompleted
            if (settings.keyboardOpenCount > 0) {
                prefs[KEY_KEYBOARD_OPEN_COUNT] = settings.keyboardOpenCount
            }

            prefs[KEY_CUSTOM_THEME_ENABLED] = settings.customThemeEnabled
            prefs[KEY_CUSTOM_KEYBOARD_BG] = settings.customKeyboardBgColor
            prefs[KEY_CUSTOM_KEY_BG] = settings.customKeyBgColor
            prefs[KEY_CUSTOM_KEY_ACTION_BG] = settings.customKeyActionBgColor
            prefs[KEY_CUSTOM_TEXT_COLOR] = settings.customTextColor
            prefs[KEY_CUSTOM_SECONDARY_TEXT_COLOR] = settings.customSecondaryTextColor
            prefs[KEY_CUSTOM_ACCENT_COLOR] = settings.customAccentColor
            prefs[KEY_CUSTOM_SUGG_BG] = settings.customSuggestionBgColor
            prefs[KEY_CUSTOM_BG_IMAGE_URI] = settings.customBackgroundImageUri
            prefs[KEY_CUSTOM_BG_DIM] = settings.customBackgroundDim
            prefs[KEY_CUSTOM_BG_BLUR] = settings.customBackgroundBlur
            prefs[KEY_CUSTOM_ICON_STYLE] = settings.customThemeIconStyle
            prefs[KEY_CUSTOM_BORDER_WIDTH] = settings.customKeyBorderWidthDp
            prefs[KEY_CUSTOM_BORDER_COLOR] = settings.customKeyBorderColor
            prefs[KEY_KEY_ELEVATION] = settings.keyElevationDp

            prefs[KEY_WIDTH_RATIO] = settings.keyboardWidthRatio
            prefs[KEY_KEY_CORNER_RADIUS] = settings.keyCornerRadiusDp
            prefs[KEY_KEY_VERTICAL_GAP] = settings.keyVerticalGapDp
            prefs[KEY_KEY_HORIZONTAL_GAP] = settings.keyHorizontalGapDp
            prefs[KEY_SIDE_MARGIN] = settings.keyboardSideMarginDp
            prefs[KEY_BOTTOM_MARGIN] = settings.keyboardBottomMarginDp
            prefs[KEY_KEY_FONT_SIZE_RATIO] = settings.keyFontSizeRatio

            prefs[KEY_ENABLED_SHORTCUTS] = settings.enabledShortcuts
            prefs[KEY_SHORTCUT_ORDER] = settings.shortcutOrder
            prefs[KEY_SHOW_SUGG_MIC] = settings.showSuggestionMicIcon
            prefs[KEY_SHOW_SUGG_VIDEO] = settings.showSuggestionVideoIcon
            prefs[KEY_SHOW_QUICK_PASTE] = settings.showQuickPasteChip
            prefs[KEY_TOOLBAR_POSITION] = settings.toolbarPosition
        }
    }
}
