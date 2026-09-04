package com.example.theme

import androidx.compose.ui.graphics.Color

data class KeyboardPalette(
    val keyboardBackground: Color,
    val keyBackground: Color,
    val keyPressedBackground: Color,
    val keyActionBackground: Color,
    val textColor: Color,
    val secondaryTextColor: Color,
    val accentColor: Color,
    val onAccentColor: Color = Color(0xFF003258),
    val suggestionBarBackground: Color,
    val suggestionHighlightColor: Color,
    val dividerColor: Color
)

object KeyboardThemes {
    // Geometric Balance: crisp charcoal, precision function keys, and soft azure accents
    val GeometricBalance = KeyboardPalette(
        keyboardBackground = Color(0xFF1A1C1E),
        keyBackground = Color(0xFF2D2F31),
        keyPressedBackground = Color(0xFF4E5154),
        keyActionBackground = Color(0xFF3E4144),
        textColor = Color(0xFFE2E2E6),
        secondaryTextColor = Color(0xFF909094),
        accentColor = Color(0xFFD1E4FF),
        onAccentColor = Color(0xFF003258),
        suggestionBarBackground = Color(0xFF1A1C1E),
        suggestionHighlightColor = Color(0xFFD1E4FF),
        dividerColor = Color(0xFF44474E)
    )

    val Dark = GeometricBalance

    val Light = KeyboardPalette(
        keyboardBackground = Color(0xFFECEFF1),
        keyBackground = Color(0xFFFFFFFF),
        keyPressedBackground = Color(0xFFCFD8DC),
        keyActionBackground = Color(0xFFDFE4E8),
        textColor = Color(0xFF1E293B),
        secondaryTextColor = Color(0xFF64748B),
        accentColor = Color(0xFF0284C7),
        onAccentColor = Color(0xFFFFFFFF),
        suggestionBarBackground = Color(0xFFE2E8F0),
        suggestionHighlightColor = Color(0xFF38BDF8),
        dividerColor = Color(0x1F000000)
    )

    val Amoled = KeyboardPalette(
        keyboardBackground = Color(0xFF000000),
        keyBackground = Color(0xFF121212),
        keyPressedBackground = Color(0xFF242424),
        keyActionBackground = Color(0xFF0A0A0A),
        textColor = Color(0xFFFFFFFF),
        secondaryTextColor = Color(0xFF888888),
        accentColor = Color(0xFFD1E4FF),
        onAccentColor = Color(0xFF003258),
        suggestionBarBackground = Color(0xFF000000),
        suggestionHighlightColor = Color(0xFFD1E4FF),
        dividerColor = Color(0x33333333)
    )

    val Custom = KeyboardPalette(
        keyboardBackground = Color(0xFF1A1D24),
        keyBackground = Color(0xFF2B313D),
        keyPressedBackground = Color(0xFF3E4657),
        keyActionBackground = Color(0xFF212630),
        textColor = Color(0xFFF1F5F9),
        secondaryTextColor = Color(0xFFA5B4FC),
        accentColor = Color(0xFF818CF8),
        onAccentColor = Color(0xFF1E1B4B),
        suggestionBarBackground = Color(0xFF14171E),
        suggestionHighlightColor = Color(0xFF6366F1),
        dividerColor = Color(0x22818CF8)
    )

    fun getPalette(themeName: String): KeyboardPalette {
        return when (themeName.lowercase()) {
            "geometric", "geometric_balance", "dark" -> GeometricBalance
            "light" -> Light
            "amoled" -> Amoled
            "custom" -> Custom
            else -> GeometricBalance
        }
    }
}

