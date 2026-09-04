package com.example.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ThemeSpecialIconStyle {
    STANDARD,
    PUPPY_MINIMAL,
    STRAWBERRY_DESSERT
}

enum class KeyPopupStyle {
    STANDARD,
    PUPPY_CHARACTER,
    STRAWBERRY_SWEET
}

enum class SpacebarStyle {
    STANDARD_BAR,
    PUPPY_BRACKET,
    STRAWBERRY_PILL
}

data class KeyboardPalette(
    val themeId: String = "geometric",
    val themeName: String = "Geometric Balance",
    val themeDescription: String = "Crisp charcoal, precision function keys, and soft azure accents",
    val category: String = "Modern & Dark",
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
    val dividerColor: Color,
    val keyBorderColor: Color = Color(0x3344474E),
    val keyBorderWidth: Dp = 0.5.dp,
    val keyCornerRadius: Dp = 8.dp,
    val keyElevation: Dp = 1.5.dp,
    val pressedElevation: Dp = 0.5.dp,
    val specialIconStyle: ThemeSpecialIconStyle = ThemeSpecialIconStyle.STANDARD,
    val popupStyle: KeyPopupStyle = KeyPopupStyle.STANDARD,
    val spacebarStyle: SpacebarStyle = SpacebarStyle.STANDARD_BAR,
    val spacebarWatermark: String? = null,
    val showTopRowHints: Boolean = true,
    val topRowHintColor: Color? = null,
    val previewColors: List<Color> = listOf(
        keyboardBackground,
        keyBackground,
        accentColor,
        textColor
    )
)

object KeyboardThemes {

    // 1. Puppy Pop (3D Neumorphic White with Puppy Popup) - Reference Image 1
    val PuppyPop = KeyboardPalette(
        themeId = "puppy_pop",
        themeName = "Puppy Pop White",
        themeDescription = "3D soft neumorphic white with adorable puppy popups and sleek grey accents",
        category = "Illustrated & 3D",
        keyboardBackground = Color(0xFFEBEDF0),
        keyBackground = Color(0xFFFFFFFF),
        keyPressedBackground = Color(0xFF1E88E5), // Vibrant blue on press
        keyActionBackground = Color(0xFFD3D8DF), // Soft cool grey function keys
        textColor = Color(0xFF1E242B),
        secondaryTextColor = Color(0xFF7A838F),
        accentColor = Color(0xFF1E88E5), // Playful sky blue accent
        onAccentColor = Color(0xFFFFFFFF),
        suggestionBarBackground = Color(0xFFE2E6EC),
        suggestionHighlightColor = Color(0xFF1E88E5),
        dividerColor = Color(0x22000000),
        keyBorderColor = Color(0x18000000),
        keyBorderWidth = 1.dp,
        keyCornerRadius = 10.dp,
        keyElevation = 2.5.dp,
        pressedElevation = 0.dp,
        specialIconStyle = ThemeSpecialIconStyle.PUPPY_MINIMAL,
        popupStyle = KeyPopupStyle.PUPPY_CHARACTER,
        spacebarStyle = SpacebarStyle.PUPPY_BRACKET,
        spacebarWatermark = "nxv",
        showTopRowHints = true,
        topRowHintColor = Color(0xFF8C95A3),
        previewColors = listOf(
            Color(0xFFEBEDF0),
            Color(0xFFFFFFFF),
            Color(0xFF1E88E5),
            Color(0xFFD3D8DF)
        )
    )

    // 2. Strawberry Dessert (Sweet Pink & Desserts) - Reference Image 2
    val StrawberryDessert = KeyboardPalette(
        themeId = "strawberry_dessert",
        themeName = "Strawberry Dessert",
        themeDescription = "Sweet strawberry pink keys with berry outlines, cute cakes, frappe & dessert icons",
        category = "Illustrated & Cute",
        keyboardBackground = Color(0xFFFFFFFF),
        keyBackground = Color(0xFFFFE5EB), // Soft pink interior
        keyPressedBackground = Color(0xFFFF7597), // Rosy pink press
        keyActionBackground = Color(0xFFFFFFFF), // Crisp dessert white action keys
        textColor = Color(0xFF6B1220), // Rich dark strawberry chocolate / wine
        secondaryTextColor = Color(0xFFC75267),
        accentColor = Color(0xFFE5395A), // Strawberry red accent
        onAccentColor = Color(0xFFFFFFFF),
        suggestionBarBackground = Color(0xFFFFF0F3),
        suggestionHighlightColor = Color(0xFFE5395A),
        dividerColor = Color(0x2BD15B70),
        keyBorderColor = Color(0xFFD15B70), // Strawberry pink-red border
        keyBorderWidth = 1.5.dp,
        keyCornerRadius = 11.dp,
        keyElevation = 1.dp,
        pressedElevation = 0.5.dp,
        specialIconStyle = ThemeSpecialIconStyle.STRAWBERRY_DESSERT,
        popupStyle = KeyPopupStyle.STRAWBERRY_SWEET,
        spacebarStyle = SpacebarStyle.STRAWBERRY_PILL,
        spacebarWatermark = null,
        showTopRowHints = true,
        topRowHintColor = Color(0xFFD15B70),
        previewColors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFFFE5EB),
            Color(0xFFD15B70),
            Color(0xFF6B1220)
        )
    )

    // 3. Geometric Balance (Default Theme)
    val GeometricBalance = KeyboardPalette(
        themeId = "geometric",
        themeName = "Geometric Balance",
        themeDescription = "Crisp charcoal, precision function keys, and soft azure accents",
        category = "Modern & Dark",
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
        dividerColor = Color(0xFF44474E),
        keyBorderColor = Color(0x3344474E),
        keyBorderWidth = 0.5.dp,
        keyCornerRadius = 8.dp,
        keyElevation = 1.5.dp,
        previewColors = listOf(
            Color(0xFF1A1C1E),
            Color(0xFF2D2F31),
            Color(0xFFD1E4FF),
            Color(0xFFE2E2E6)
        )
    )

    val Dark = GeometricBalance

    // 4. Light Crisp
    val Light = KeyboardPalette(
        themeId = "light",
        themeName = "Light Crisp",
        themeDescription = "Clean daylight slate with azure highlights",
        category = "Light & Clean",
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
        dividerColor = Color(0x1F000000),
        keyBorderColor = Color(0x1A000000),
        keyBorderWidth = 0.5.dp,
        keyCornerRadius = 8.dp,
        keyElevation = 1.5.dp,
        previewColors = listOf(
            Color(0xFFECEFF1),
            Color(0xFFFFFFFF),
            Color(0xFF0284C7),
            Color(0xFF1E293B)
        )
    )

    // 5. AMOLED Pure Black
    val Amoled = KeyboardPalette(
        themeId = "amoled",
        themeName = "AMOLED Pure Black",
        themeDescription = "Deep true black designed for maximum battery saving on OLED screens",
        category = "Modern & Dark",
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
        dividerColor = Color(0x33333333),
        keyBorderColor = Color(0x22FFFFFF),
        keyBorderWidth = 0.5.dp,
        keyCornerRadius = 8.dp,
        keyElevation = 0.dp,
        previewColors = listOf(
            Color(0xFF000000),
            Color(0xFF121212),
            Color(0xFFD1E4FF),
            Color(0xFFFFFFFF)
        )
    )

    // 6. Indigo Glow
    val Custom = KeyboardPalette(
        themeId = "custom",
        themeName = "Indigo Glow",
        themeDescription = "Vibrant deep violet with neon indigo accents",
        category = "Vibrant & Neon",
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
        dividerColor = Color(0x22818CF8),
        keyBorderColor = Color(0x33818CF8),
        keyBorderWidth = 0.5.dp,
        keyCornerRadius = 8.dp,
        keyElevation = 1.5.dp,
        previewColors = listOf(
            Color(0xFF1A1D24),
            Color(0xFF2B313D),
            Color(0xFF818CF8),
            Color(0xFFF1F5F9)
        )
    )

    val ALL_THEMES: List<KeyboardPalette> = listOf(
        PuppyPop,
        StrawberryDessert,
        GeometricBalance,
        Light,
        Amoled,
        Custom
    )

    fun getPalette(themeName: String): KeyboardPalette {
        return when (themeName.lowercase()) {
            "puppy_pop", "puppypop", "puppy", "puppy_white" -> PuppyPop
            "strawberry_dessert", "strawberry", "dessert", "strawberry_pink" -> StrawberryDessert
            "geometric", "geometric_balance", "dark" -> GeometricBalance
            "light" -> Light
            "amoled" -> Amoled
            "custom", "indigo" -> Custom
            else -> GeometricBalance
        }
    }
}
