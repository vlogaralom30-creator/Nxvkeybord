package com.example.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Ridmik Theme Specifications & Spacing System
 * Replicates the authentic color palette, button spacing, and typography
 * of the Ridmik Bengali / English keyboard as requested.
 *
 * Provides a "Double Theme" architecture:
 * - "Original Ridmik": Authentic Jet-Black canvas (#000000), matte slate keycaps (#48484A),
 *   charcoal modifier keys (#262628), compact 4.5dp corner radius, 3dp horizontal key gap,
 *   5dp row gap, top-right secondary symbol hints, bottom-right "..." dots, and "◄ English ►"
 *   / "◄ বাংলা ►" / "◄ প্রভাত ►" spacebar navigation.
 * - "NXV Enhanced": Modern neon/azure glassmorphic styling, 8dp rounded corner keycaps,
 *   subtle light borders, high-fidelity glowing feedback, and floating toolbar icons.
 */
@Immutable
data class RidmikColorPalette(
    val background: Color,
    val keyAlphaBackground: Color,
    val keyAlphaPressed: Color,
    val keyModifierBackground: Color,
    val keyModifierPressed: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val suggestionBackground: Color,
    val suggestionDivider: Color,
    val spacebarBackground: Color,
    val keyBorderColor: Color = Color.Transparent
)

@Immutable
data class RidmikSpacingSpecs(
    val keyCornerRadius: Dp = 4.5.dp,
    val keyHorizontalGap: Dp = 3.dp,
    val keyVerticalGap: Dp = 5.dp,
    val keyboardHorizontalPadding: Dp = 2.dp,
    val keyboardVerticalPadding: Dp = 3.dp,
    val keyElevation: Dp = 0.5.dp,
    val pressedElevation: Dp = 0.dp,
    val keyBorderWidth: Dp = 0.dp,
    val spacebarArrows: Boolean = true,
    val showCornerDots: Boolean = true
)

object RidmikThemes {

    // =========================================================================
    // COLOR PALETTES (Replicating user screenshots 1, 2, 3, 4)
    // =========================================================================

    // Authentic Ridmik Pure Black (Screenshots 1, 2, 4)
    val OriginalDarkColors = RidmikColorPalette(
        background = Color(0xFF000000),            // Pure Jet Black
        keyAlphaBackground = Color(0xFF48484A),    // Matte slate grey for alpha keys & space
        keyAlphaPressed = Color(0xFF6B6B70),       // Lighter slate when pressed
        keyModifierBackground = Color(0xFF262628), // Dark charcoal for Shift, Backspace, ?123, Emoji
        keyModifierPressed = Color(0xFF3E3E42),
        textPrimary = Color(0xFFFFFFFF),           // Crisp white legends
        textSecondary = Color(0xFF9A9A9E),         // Medium grey top-right hints & dots
        accent = Color(0xFF0A84FF),                // iOS/Material Vivid Blue
        suggestionBackground = Color(0xFF18181A),  // Suggestion bar background
        suggestionDivider = Color(0xFF28282C),     // Vertical separator between words
        spacebarBackground = Color(0xFF48484A)
    )

    // Probhat Bengali Classic (Screenshot 3)
    val ProbhatColors = RidmikColorPalette(
        background = Color(0xFF000000),
        keyAlphaBackground = Color(0xFF48484A),
        keyAlphaPressed = Color(0xFF6B6B70),
        keyModifierBackground = Color(0xFF262628),
        keyModifierPressed = Color(0xFF3E3E42),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF9A9A9E),
        accent = Color(0xFF00BFA5),                // Classic Teal accent
        suggestionBackground = Color(0xFF18181A),
        suggestionDivider = Color(0xFF28282C),
        spacebarBackground = Color(0xFF48484A)
    )

    // Ridmik Clean White (Light Theme)
    val OriginalWhiteColors = RidmikColorPalette(
        background = Color(0xFFECEFF1),            // Soft grey-white canvas
        keyAlphaBackground = Color(0xFFFFFFFF),    // Pure white keycaps
        keyAlphaPressed = Color(0xFFD6DBDF),
        keyModifierBackground = Color(0xFFCFD8DC), // Soft slate modifier keys
        keyModifierPressed = Color(0xFFB0BEC5),
        textPrimary = Color(0xFF263238),           // Deep charcoal text
        textSecondary = Color(0xFF78909C),
        accent = Color(0xFF0A84FF),
        suggestionBackground = Color(0xFFF5F7FA),
        suggestionDivider = Color(0xFFCFD8DC),
        spacebarBackground = Color(0xFFFFFFFF),
        keyBorderColor = Color(0x18000000)
    )

    // NXV Modern Dark (Double theme counterpart)
    val NxvModernColors = RidmikColorPalette(
        background = Color(0xFF121316),
        keyAlphaBackground = Color(0xFF20232A),
        keyAlphaPressed = Color(0xFF323742),
        keyModifierBackground = Color(0xFF181A1F),
        keyModifierPressed = Color(0xFF2A2D35),
        textPrimary = Color(0xFFF0F4F8),
        textSecondary = Color(0xFF7C889B),
        accent = Color(0xFF00D2FF),                // Electric Cyan
        suggestionBackground = Color(0xFF16181D),
        suggestionDivider = Color(0x33323742),
        spacebarBackground = Color(0xFF20232A),
        keyBorderColor = Color(0x2AFFFFFF)
    )

    // =========================================================================
    // BUTTON SPACING SPECIFICATIONS
    // =========================================================================

    // Authentic Ridmik Spacing: Compact, seamless, 4.5dp radius, 3dp horizontal gap
    val OriginalSpacing = RidmikSpacingSpecs(
        keyCornerRadius = 4.5.dp,
        keyHorizontalGap = 3.dp,
        keyVerticalGap = 5.dp,
        keyboardHorizontalPadding = 2.dp,
        keyboardVerticalPadding = 3.dp,
        keyElevation = 0.5.dp,
        pressedElevation = 0.dp,
        keyBorderWidth = 0.dp,
        spacebarArrows = true,
        showCornerDots = true
    )

    // NXV Modern Spacing: 8dp radius, modern card spacing, soft borders
    val NxvSpacing = RidmikSpacingSpecs(
        keyCornerRadius = 8.dp,
        keyHorizontalGap = 4.dp,
        keyVerticalGap = 6.dp,
        keyboardHorizontalPadding = 4.dp,
        keyboardVerticalPadding = 4.dp,
        keyElevation = 2.dp,
        pressedElevation = 0.5.dp,
        keyBorderWidth = 0.5.dp,
        spacebarArrows = false,
        showCornerDots = false
    )

    // =========================================================================
    // READY-TO-USE KEYBOARD PALETTES (Integrating into KeyboardThemes engine)
    // =========================================================================

    val RidmikOriginalDarkPalette = KeyboardPalette(
        themeId = "ridmik_original",
        themeName = "Original Ridmik (Dark)",
        themeDescription = "1:1 Authentic Ridmik experience: pure black canvas, matte slate keys, 4.5dp compact spacing, and ◄ English ► / ◄ বাংলা ► bar",
        category = "Ridmik & Bengali",
        keyboardBackground = OriginalDarkColors.background,
        keyBackground = OriginalDarkColors.keyAlphaBackground,
        keyPressedBackground = OriginalDarkColors.keyAlphaPressed,
        keyActionBackground = OriginalDarkColors.keyModifierBackground,
        textColor = OriginalDarkColors.textPrimary,
        secondaryTextColor = OriginalDarkColors.textSecondary,
        accentColor = OriginalDarkColors.accent,
        onAccentColor = Color.White,
        suggestionBarBackground = OriginalDarkColors.suggestionBackground,
        suggestionHighlightColor = OriginalDarkColors.accent,
        dividerColor = OriginalDarkColors.suggestionDivider,
        keyBorderColor = Color.Transparent,
        keyBorderWidth = 0.dp,
        keyCornerRadius = OriginalSpacing.keyCornerRadius,
        keyElevation = OriginalSpacing.keyElevation,
        pressedElevation = OriginalSpacing.pressedElevation,
        specialIconStyle = ThemeSpecialIconStyle.STANDARD,
        popupStyle = KeyPopupStyle.STANDARD,
        spacebarStyle = SpacebarStyle.STANDARD_BAR,
        spacebarWatermark = "বাংলা",
        showTopRowHints = true,
        topRowHintColor = OriginalDarkColors.textSecondary,
        previewColors = listOf(
            OriginalDarkColors.background,
            OriginalDarkColors.keyAlphaBackground,
            OriginalDarkColors.keyModifierBackground,
            OriginalDarkColors.accent
        )
    )

    val RidmikProbhatPalette = KeyboardPalette(
        themeId = "ridmik_probhat",
        themeName = "Ridmik Probhat (Classic)",
        themeDescription = "Authentic Probhat layout theme: pure black canvas, teal accent, and ◄ প্রভাত ► spacebar navigation",
        category = "Ridmik & Bengali",
        keyboardBackground = ProbhatColors.background,
        keyBackground = ProbhatColors.keyAlphaBackground,
        keyPressedBackground = ProbhatColors.keyAlphaPressed,
        keyActionBackground = ProbhatColors.keyModifierBackground,
        textColor = ProbhatColors.textPrimary,
        secondaryTextColor = ProbhatColors.textSecondary,
        accentColor = ProbhatColors.accent,
        onAccentColor = Color.White,
        suggestionBarBackground = ProbhatColors.suggestionBackground,
        suggestionHighlightColor = ProbhatColors.accent,
        dividerColor = ProbhatColors.suggestionDivider,
        keyBorderColor = Color.Transparent,
        keyBorderWidth = 0.dp,
        keyCornerRadius = OriginalSpacing.keyCornerRadius,
        keyElevation = OriginalSpacing.keyElevation,
        pressedElevation = OriginalSpacing.pressedElevation,
        specialIconStyle = ThemeSpecialIconStyle.STANDARD,
        popupStyle = KeyPopupStyle.STANDARD,
        spacebarStyle = SpacebarStyle.STANDARD_BAR,
        spacebarWatermark = "প্রভাত",
        showTopRowHints = true,
        topRowHintColor = ProbhatColors.textSecondary,
        previewColors = listOf(
            ProbhatColors.background,
            ProbhatColors.keyAlphaBackground,
            ProbhatColors.keyModifierBackground,
            ProbhatColors.accent
        )
    )

    val RidmikOriginalWhitePalette = KeyboardPalette(
        themeId = "ridmik_white",
        themeName = "Original Ridmik (White)",
        themeDescription = "Crisp light Ridmik theme: pure white keycaps, slate modifier accents, and compact spacing",
        category = "Ridmik & Bengali",
        keyboardBackground = OriginalWhiteColors.background,
        keyBackground = OriginalWhiteColors.keyAlphaBackground,
        keyPressedBackground = OriginalWhiteColors.keyAlphaPressed,
        keyActionBackground = OriginalWhiteColors.keyModifierBackground,
        textColor = OriginalWhiteColors.textPrimary,
        secondaryTextColor = OriginalWhiteColors.textSecondary,
        accentColor = OriginalWhiteColors.accent,
        onAccentColor = Color.White,
        suggestionBarBackground = OriginalWhiteColors.suggestionBackground,
        suggestionHighlightColor = OriginalWhiteColors.accent,
        dividerColor = OriginalWhiteColors.suggestionDivider,
        keyBorderColor = OriginalWhiteColors.keyBorderColor,
        keyBorderWidth = 0.5.dp,
        keyCornerRadius = OriginalSpacing.keyCornerRadius,
        keyElevation = 1.dp,
        pressedElevation = 0.5.dp,
        specialIconStyle = ThemeSpecialIconStyle.STANDARD,
        popupStyle = KeyPopupStyle.STANDARD,
        spacebarStyle = SpacebarStyle.STANDARD_BAR,
        spacebarWatermark = "বাংলা",
        showTopRowHints = true,
        topRowHintColor = OriginalWhiteColors.textSecondary,
        previewColors = listOf(
            OriginalWhiteColors.background,
            OriginalWhiteColors.keyAlphaBackground,
            OriginalWhiteColors.keyModifierBackground,
            OriginalWhiteColors.accent
        )
    )

    val NxvEnhancedPalette = KeyboardPalette(
        themeId = "nxv_enhanced",
        themeName = "NXV Enhanced (Modern)",
        themeDescription = "NXV modern aesthetic: glassmorphic borders, 8dp rounded keys, cyan accents, and floating controls",
        category = "Modern & Dark",
        keyboardBackground = NxvModernColors.background,
        keyBackground = NxvModernColors.keyAlphaBackground,
        keyPressedBackground = NxvModernColors.keyAlphaPressed,
        keyActionBackground = NxvModernColors.keyModifierBackground,
        textColor = NxvModernColors.textPrimary,
        secondaryTextColor = NxvModernColors.textSecondary,
        accentColor = NxvModernColors.accent,
        onAccentColor = Color(0xFF003258),
        suggestionBarBackground = NxvModernColors.suggestionBackground,
        suggestionHighlightColor = NxvModernColors.accent,
        dividerColor = NxvModernColors.suggestionDivider,
        keyBorderColor = NxvModernColors.keyBorderColor,
        keyBorderWidth = NxvSpacing.keyBorderWidth,
        keyCornerRadius = NxvSpacing.keyCornerRadius,
        keyElevation = NxvSpacing.keyElevation,
        pressedElevation = NxvSpacing.pressedElevation,
        specialIconStyle = ThemeSpecialIconStyle.STANDARD,
        popupStyle = KeyPopupStyle.STANDARD,
        spacebarStyle = SpacebarStyle.STANDARD_BAR,
        spacebarWatermark = "NXV",
        showTopRowHints = true,
        topRowHintColor = NxvModernColors.textSecondary,
        previewColors = listOf(
            NxvModernColors.background,
            NxvModernColors.keyAlphaBackground,
            NxvModernColors.keyModifierBackground,
            NxvModernColors.accent
        )
    )

    // =========================================================================
    // RIDMIK SECONDARY SYMBOLS & HINTS (Exact character placement from images)
    // =========================================================================

    // Number Row (Screenshot 4: 1..0 with Bengali numerals as top-right subLabel)
    val NUMBER_ROW_BANGLA_HINTS = listOf("১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯", "০")

    // QWERTY Row 1 hints (Screenshot 4: ৳, %, -, ;, <, >, +, =, [, ])
    val ROW1_SYMBOLS_FULL = listOf("৳", "%", "-", ";", "<", ">", "+", "=", "[", "]")

    // ASDF Row 2 hints (Screenshot 1 & 4: @, #, &, *, -, !, ?, (, ))
    val ROW2_SYMBOLS = listOf("@", "#", "&", "*", "-", "!", "?", "(", ")")

    // ZXCV Row 3 hints (Screenshot 1 & 4: ", ', :, ;, ,, ., /)
    val ROW3_SYMBOLS = listOf("\"", "'", ":", ";", ",", ".", "/")

    // Spacebar formatted text helpers
    fun formatSpacebarLabel(language: String, isAvro: Boolean): String {
        return when {
            isAvro -> "◄ বাংলা ►"
            language.equals("bangla", ignoreCase = true) -> "◄ প্রভাত ►"
            language.equals("avro", ignoreCase = true) -> "◄ অভ্র ►"
            else -> "◄ English ►"
        }
    }
}

/**
 * Composition Locals to provide Ridmik styling anywhere in the Compose tree
 */
val LocalRidmikColors = staticCompositionLocalOf { RidmikThemes.OriginalDarkColors }
val LocalRidmikSpacing = staticCompositionLocalOf { RidmikThemes.OriginalSpacing }

@Composable
fun RidmikKeyboardTheme(
    colors: RidmikColorPalette = RidmikThemes.OriginalDarkColors,
    spacing: RidmikSpacingSpecs = RidmikThemes.OriginalSpacing,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalRidmikColors provides colors,
        LocalRidmikSpacing provides spacing,
        content = content
    )
}
