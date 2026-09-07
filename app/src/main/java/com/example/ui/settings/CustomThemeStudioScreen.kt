package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchVideo
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.preferences.KeyboardSettings
import com.example.keyboard.ShiftState
import com.example.theme.KeyboardPalette
import com.example.theme.KeyboardThemes
import com.example.theme.ThemeSpecialIconStyle
import com.example.ui.keyboard.QwertyKeyLayout

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CustomThemeStudioScreen(
    settings: KeyboardSettings,
    onUpdateCustomThemeEnabled: (Boolean) -> Unit,
    onUpdateCustomKeyboardBg: (Long) -> Unit,
    onUpdateCustomKeyBg: (Long) -> Unit,
    onUpdateCustomKeyActionBg: (Long) -> Unit,
    onUpdateCustomTextColor: (Long) -> Unit,
    onUpdateCustomSecondaryTextColor: (Long) -> Unit,
    onUpdateCustomAccentColor: (Long) -> Unit,
    onUpdateCustomSuggestionBg: (Long) -> Unit,
    onUpdateCustomBackgroundImageUri: (String) -> Unit,
    onUpdateCustomBackgroundDim: (Float) -> Unit,
    onUpdateCustomBackgroundBlur: (Float) -> Unit,
    onUpdateCustomIconStyle: (String) -> Unit,
    onUpdateCustomKeyBorderWidthDp: (Float) -> Unit,
    onUpdateCustomKeyBorderColor: (Long) -> Unit,
    onUpdateKeyElevationDp: (Float) -> Unit,
    onUpdateKeyboardHeightRatio: (Float) -> Unit,
    onUpdateKeyboardWidthRatio: (Float) -> Unit,
    onUpdateKeyCornerRadiusDp: (Int) -> Unit,
    onUpdateKeyVerticalGapDp: (Int) -> Unit,
    onUpdateKeyHorizontalGapDp: (Int) -> Unit,
    onUpdateKeyboardSideMarginDp: (Int) -> Unit,
    onUpdateKeyboardBottomMarginDp: (Int) -> Unit,
    onUpdateKeyFontSizeRatio: (Float) -> Unit,
    onUpdateEnabledShortcuts: (Set<String>) -> Unit,
    onUpdateShortcutOrder: (String) -> Unit,
    onUpdateShowSuggestionMic: (Boolean) -> Unit,
    onUpdateShowSuggestionVideo: (Boolean) -> Unit,
    onUpdateShowQuickPaste: (Boolean) -> Unit,
    onUpdateToolbarPosition: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var previewShift by remember { mutableStateOf(ShiftState.LOWERCASE) }
    var testInputText by remember { mutableStateOf("") }
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdateCustomBackgroundImageUri(uri.toString())
            onUpdateCustomThemeEnabled(true)
        }
    }

    val livePalette = remember(
        settings.customThemeEnabled,
        settings.customKeyboardBgColor,
        settings.customKeyBgColor,
        settings.customKeyActionBgColor,
        settings.customTextColor,
        settings.customSecondaryTextColor,
        settings.customAccentColor,
        settings.customSuggestionBgColor,
        settings.customThemeIconStyle,
        settings.keyCornerRadiusDp,
        settings.customKeyBorderWidthDp,
        settings.customKeyBorderColor,
        settings.keyElevationDp,
        settings.theme
    ) {
        if (settings.customThemeEnabled) {
            KeyboardThemes.getPalette("custom_diy", settings)
        } else {
            KeyboardThemes.getPalette(settings.theme, settings)
        }
    }

    val tabTitles = listOf(
        "🎨 Colors & Font",
        "🖼️ Photo BG",
        "📐 Sizing & Gap",
        "🛠️ Shortcuts",
        "🎭 Theme Icons"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Custom Theme & Layout Studio", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("মন মতো কিবোর্ড সাজিয়ে নিন (Full Customization)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top Sticky Live Interactive Keyboard Preview Box
            Card(
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✨ Live Preview", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            if (settings.customThemeEnabled) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("CUSTOM DIY ACTIVE", color = MaterialTheme.colorScheme.onPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Height: ${(settings.keyboardHeightRatio * 100).toInt()}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Width: ${(settings.keyboardWidthRatio * 100).toInt()}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Keyboard Live Container Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(settings.keyboardWidthRatio)
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = (settings.keyboardSideMarginDp / 2).dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(livePalette.keyboardBackground)
                    ) {
                        // Background Photo Overlay if present
                        if (settings.customBackgroundImageUri.isNotBlank()) {
                            AsyncImage(
                                model = settings.customBackgroundImageUri,
                                contentDescription = "Custom Photo Background",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .matchParentSize()
                                    .blur(if (settings.customBackgroundBlur > 0) settings.customBackgroundBlur.dp else 0.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = settings.customBackgroundDim))
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Mini Suggestion Bar & Quick Action Strip Preview
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(livePalette.suggestionBarBackground)
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(livePalette.accentColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("বাংলা", color = livePalette.accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Hello", color = livePalette.textColor, fontSize = 12.sp)
                                    Text("কেমন আছো", color = livePalette.accentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (settings.showSuggestionVideoIcon) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = livePalette.textColor.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                    }
                                    if (settings.showSuggestionMicIcon) {
                                        Icon(Icons.Default.Mic, contentDescription = null, tint = livePalette.accentColor, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            // Interactive Qwerty Keys Preview
                            QwertyKeyLayout(
                                isAvro = false,
                                shiftState = previewShift,
                                showNumberRow = settings.showNumberRow,
                                keyHeight = (32 * settings.keyboardHeightRatio).dp,
                                palette = livePalette,
                                enterLabel = "↵",
                                showEmojiKey = settings.showEmojiKey,
                                showLanguageKey = settings.showLanguageKey,
                                showKeySubLabels = settings.showKeySubLabels,
                                popupMode = settings.keyPopupMode,
                                onCharTyped = { char -> testInputText += char },
                                onDelete = { if (testInputText.isNotEmpty()) testInputText = testInputText.dropLast(1) },
                                onSpace = { testInputText += " " },
                                onEnter = { testInputText += "\n" },
                                onShift = {
                                    previewShift = if (previewShift == ShiftState.LOWERCASE) ShiftState.SHIFT_ONCE else ShiftState.LOWERCASE
                                },
                                onSwitchMode = { /* preview mode */ },
                                onLanguageCycle = { /* preview cycle */ },
                                onLongPressLanguage = { /* preview long press */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = (settings.keyVerticalGapDp / 2).dp, horizontal = (settings.keyHorizontalGapDp / 2).dp)
                            )
                        }
                    }

                    // Test typed output bar
                    if (testInputText.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Typed: $testInputText",
                                fontSize = 12.sp,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Clear",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { testInputText = "" }
                            )
                        }
                    }
                }
            }

            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp)
            ) {
                when (selectedTab) {
                    0 -> ColorsTabContent(
                        settings = settings,
                        onUpdateCustomThemeEnabled = onUpdateCustomThemeEnabled,
                        onUpdateCustomKeyboardBg = onUpdateCustomKeyboardBg,
                        onUpdateCustomKeyBg = onUpdateCustomKeyBg,
                        onUpdateCustomKeyActionBg = onUpdateCustomKeyActionBg,
                        onUpdateCustomTextColor = onUpdateCustomTextColor,
                        onUpdateCustomSecondaryTextColor = onUpdateCustomSecondaryTextColor,
                        onUpdateCustomAccentColor = onUpdateCustomAccentColor,
                        onUpdateCustomSuggestionBg = onUpdateCustomSuggestionBg,
                        onUpdateCustomKeyBorderColor = onUpdateCustomKeyBorderColor
                    )
                    1 -> PhotoBackgroundTabContent(
                        settings = settings,
                        onPickPhoto = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onUpdateBackgroundDim = onUpdateCustomBackgroundDim,
                        onUpdateBackgroundBlur = onUpdateCustomBackgroundBlur,
                        onRemovePhoto = { onUpdateCustomBackgroundImageUri("") }
                    )
                    2 -> SizingTabContent(
                        settings = settings,
                        onUpdateHeightRatio = onUpdateKeyboardHeightRatio,
                        onUpdateWidthRatio = onUpdateKeyboardWidthRatio,
                        onUpdateCornerRadius = onUpdateKeyCornerRadiusDp,
                        onUpdateVerticalGap = onUpdateKeyVerticalGapDp,
                        onUpdateHorizontalGap = onUpdateKeyHorizontalGapDp,
                        onUpdateSideMargin = onUpdateKeyboardSideMarginDp,
                        onUpdateBottomMargin = onUpdateKeyboardBottomMarginDp,
                        onUpdateFontSizeRatio = onUpdateKeyFontSizeRatio,
                        onUpdateKeyElevation = onUpdateKeyElevationDp,
                        onUpdateKeyBorderWidth = onUpdateCustomKeyBorderWidthDp
                    )
                    3 -> ShortcutsTabContent(
                        settings = settings,
                        onUpdateEnabledShortcuts = onUpdateEnabledShortcuts,
                        onUpdateShortcutOrder = onUpdateShortcutOrder,
                        onUpdateShowSuggestionMic = onUpdateShowSuggestionMic,
                        onUpdateShowSuggestionVideo = onUpdateShowSuggestionVideo,
                        onUpdateShowQuickPaste = onUpdateShowQuickPaste,
                        onUpdateToolbarPosition = onUpdateToolbarPosition
                    )
                    4 -> ThemeIconsTabContent(
                        settings = settings,
                        onUpdateIconStyle = onUpdateCustomIconStyle
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 1: Colors & Fonts
// -----------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorsTabContent(
    settings: KeyboardSettings,
    onUpdateCustomThemeEnabled: (Boolean) -> Unit,
    onUpdateCustomKeyboardBg: (Long) -> Unit,
    onUpdateCustomKeyBg: (Long) -> Unit,
    onUpdateCustomKeyActionBg: (Long) -> Unit,
    onUpdateCustomTextColor: (Long) -> Unit,
    onUpdateCustomSecondaryTextColor: (Long) -> Unit,
    onUpdateCustomAccentColor: (Long) -> Unit,
    onUpdateCustomSuggestionBg: (Long) -> Unit,
    onUpdateCustomKeyBorderColor: (Long) -> Unit
) {
    val presetPalettes = listOf(
        PresetThemeBundle("Cyber Cyan", 0xFF0B0E14, 0xFF161B22, 0xFF10141B, 0xFFE6EDF3, 0xFF8B949E, 0xFF00D2FF, 0xFF10141B, 0x3300D2FF),
        PresetThemeBundle("Neon Purple", 0xFF0F081D, 0xFF1E1035, 0xFF160B28, 0xFFF3E8FF, 0xFFA855F7, 0xFFC084FC, 0xFF160B28, 0x44C084FC),
        PresetThemeBundle("Emerald Mint", 0xFF071813, 0xFF0F2E24, 0xFF0A201A, 0xFFECFDF5, 0xFF34D399, 0xFF10B981, 0xFF0A201A, 0x3310B981),
        PresetThemeBundle("Sunset Amber", 0xFF191008, 0xFF2D1E10, 0xFF22160C, 0xFFFFFBEB, 0xFFFBBF24, 0xFFF59E0B, 0xFF22160C, 0x33F59E0B),
        PresetThemeBundle("Rose Pink", 0xFF1A0A12, 0xFF301524, 0xFF240E1B, 0xFFFFF1F2, 0xFFFB7185, 0xFFF43F5E, 0xFF240E1B, 0x33F43F5E),
        PresetThemeBundle("Ridmik Classic Teal", 0xFF003838, 0xFF004D40, 0xFF00332C, 0xFFFFFFFF, 0xFF80CBC4, 0xFF00BFA5, 0xFF00332C, 0x2AFFFFFF),
        PresetThemeBundle("Deep Space Navy", 0xFF0D1B2A, 0xFF1B263B, 0xFF142032, 0xFFE0E1DD, 0xFF778DA9, 0xFF4CC9F0, 0xFF142032, 0x334CC9F0),
        PresetThemeBundle("AMOLED Pure Black", 0xFF000000, 0xFF121212, 0xFF080808, 0xFFFFFFFF, 0xFF757575, 0xFF2979FF, 0xFF080808, 0x22FFFFFF),
        PresetThemeBundle("Clean Paper Light", 0xFFF8F9FA, 0xFFFFFFFF, 0xFFE9ECEF, 0xFF212529, 0xFF6C757D, 0xFF0D6EFD, 0xFFE9ECEF, 0x33000000)
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Toggle Custom DIY Master Switch
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (settings.customThemeEnabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("কাস্টম কালার থিম মোড (Custom DIY Mode)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("আপনার সিলেক্ট করা ব্যাকগ্রাউন্ড কালার ও বাটন রঙ কার্যকর করুন", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = settings.customThemeEnabled,
                    onCheckedChange = onUpdateCustomThemeEnabled,
                    modifier = Modifier.testTag("custom_theme_master_switch")
                )
            }
        }

        // 1-Tap Preset Palette Bundles
        Text("১-ট্যাপ প্রেসেট কালার কম্বিনেশন (Preset Palettes)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetPalettes.forEach { bundle ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(bundle.kbBg)),
                    border = BorderStroke(1.2.dp, Color(bundle.accent).copy(alpha = 0.7f)),
                    modifier = Modifier
                        .clickable {
                            onUpdateCustomKeyboardBg(bundle.kbBg)
                            onUpdateCustomKeyBg(bundle.keyBg)
                            onUpdateCustomKeyActionBg(bundle.actionBg)
                            onUpdateCustomTextColor(bundle.textColor)
                            onUpdateCustomSecondaryTextColor(bundle.secColor)
                            onUpdateCustomAccentColor(bundle.accent)
                            onUpdateCustomSuggestionBg(bundle.suggBg)
                            onUpdateCustomKeyBorderColor(bundle.borderColor)
                            onUpdateCustomThemeEnabled(true)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(bundle.accent))
                        )
                        Text(
                            text = bundle.name,
                            color = Color(bundle.textColor),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Individual Color Choosers
        Text("বাটন ও টেক্সট কালার পরিবর্তন (Individual Colors)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        ColorPickerRow(
            title = "কিবোর্ড ব্যাকগ্রাউন্ড কালার (Keyboard Background)",
            currentColor = settings.customKeyboardBgColor,
            onColorSelected = {
                onUpdateCustomKeyboardBg(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        ColorPickerRow(
            title = "অক্ষর বাটন কালার (Key / Letter Buttons)",
            currentColor = settings.customKeyBgColor,
            onColorSelected = {
                onUpdateCustomKeyBg(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        ColorPickerRow(
            title = "ফাংশন বাটন কালার (Shift, Delete, 123)",
            currentColor = settings.customKeyActionBgColor,
            onColorSelected = {
                onUpdateCustomKeyActionBg(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        ColorPickerRow(
            title = "ফন্ট / অক্ষরের রঙ (Letter Font Color)",
            currentColor = settings.customTextColor,
            onColorSelected = {
                onUpdateCustomTextColor(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        ColorPickerRow(
            title = "সাব-লেবেল / হিন্ট কালার (Secondary Hints)",
            currentColor = settings.customSecondaryTextColor,
            onColorSelected = {
                onUpdateCustomSecondaryTextColor(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        ColorPickerRow(
            title = "অ্যাকসেন্ট কালার (Enter Key & Highlights)",
            currentColor = settings.customAccentColor,
            onColorSelected = {
                onUpdateCustomAccentColor(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        ColorPickerRow(
            title = "সাজেশন বার ব্যাকগ্রাউন্ড (Suggestion Strip)",
            currentColor = settings.customSuggestionBgColor,
            onColorSelected = {
                onUpdateCustomSuggestionBg(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        ColorPickerRow(
            title = "বাটনের বর্ডার কালার (Key Border Outline)",
            currentColor = settings.customKeyBorderColor,
            onColorSelected = {
                onUpdateCustomKeyBorderColor(it)
                onUpdateCustomThemeEnabled(true)
            }
        )
    }
}

private data class PresetThemeBundle(
    val name: String,
    val kbBg: Long,
    val keyBg: Long,
    val actionBg: Long,
    val textColor: Long,
    val secColor: Long,
    val accent: Long,
    val suggBg: Long,
    val borderColor: Long
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorPickerRow(
    title: String,
    currentColor: Long,
    onColorSelected: (Long) -> Unit
) {
    val swatches = listOf(
        0xFF121316, 0xFF1E222B, 0xFF000000, 0xFF0D1B2A, 0xFF003838,
        0xFF1A0A12, 0xFF1F1235, 0xFFFFFFFF, 0xFFF1F5F9, 0xFF00D2FF,
        0xFF10B981, 0xFFF59E0B, 0xFFEC4899, 0xFF8B5CF6, 0xFF3B82F6,
        0xFFEF4444, 0xFFE2E8F0, 0xFF94A3B8, 0xFF475569, 0x44FFFFFF
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(currentColor))
                        .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                swatches.forEach { colorVal ->
                    val isSelected = currentColor == colorVal
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(colorVal))
                            .border(
                                width = if (isSelected) 2.5.dp else 0.8.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(colorVal) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = if (Color(colorVal).red > 0.6f && Color(colorVal).green > 0.6f) Color.Black else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 2: Photo Background
// -----------------------------------------------------------------------------------------
@Composable
private fun PhotoBackgroundTabContent(
    settings: KeyboardSettings,
    onPickPhoto: () -> Unit,
    onUpdateBackgroundDim: (Float) -> Unit,
    onUpdateBackgroundBlur: (Float) -> Unit,
    onRemovePhoto: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("আপনার পছন্দের ছবি কিবোর্ডে যুক্ত করুন (Upload Photo)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "গ্যালারি থেকে আপনার যেকোনো পছন্দের ছবি, ওয়ালপেপার বা ফটো সিলেক্ট করুন। কিবোর্ডের পেছনে সুন্দরভাবে সেট হয়ে যাবে।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onPickPhoto,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("গ্যালারি থেকে ফটো আপলোড করুন (Pick Photo)", fontWeight = FontWeight.Bold)
                }

                if (settings.customBackgroundImageUri.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = settings.customBackgroundImageUri,
                            contentDescription = "Uploaded Background",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(if (settings.customBackgroundBlur > 0) settings.customBackgroundBlur.dp else 0.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = settings.customBackgroundDim))
                        )
                    }

                    OutlinedButton(
                        onClick = onRemovePhoto,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ছবি রিমুভ করুন (Remove Photo)")
                    }
                }
            }
        }

        if (settings.customBackgroundImageUri.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("ফটোর ডার্কনেস ও ব্লার ফিল্টার (Photo Adjustments)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    // Dim Overlay Slider
                    Text("ডার্ক ওভারলে (Dim Darkness): ${(settings.customBackgroundDim * 100).toInt()}%", fontSize = 13.sp)
                    Slider(
                        value = settings.customBackgroundDim,
                        onValueChange = onUpdateBackgroundDim,
                        valueRange = 0f..0.90f
                    )

                    // Blur Slider
                    Text("ছবি ব্লার ইফেক্ট (Background Blur): ${settings.customBackgroundBlur.toInt()} dp", fontSize = 13.sp)
                    Slider(
                        value = settings.customBackgroundBlur,
                        onValueChange = onUpdateBackgroundBlur,
                        valueRange = 0f..20f
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 3: Sizing, Scaling & Spacing (Side size% & Up-to-down size%)
// -----------------------------------------------------------------------------------------
@Composable
private fun SizingTabContent(
    settings: KeyboardSettings,
    onUpdateHeightRatio: (Float) -> Unit,
    onUpdateWidthRatio: (Float) -> Unit,
    onUpdateCornerRadius: (Int) -> Unit,
    onUpdateVerticalGap: (Int) -> Unit,
    onUpdateHorizontalGap: (Int) -> Unit,
    onUpdateSideMargin: (Int) -> Unit,
    onUpdateBottomMargin: (Int) -> Unit,
    onUpdateFontSizeRatio: (Float) -> Unit,
    onUpdateKeyElevation: (Float) -> Unit,
    onUpdateKeyBorderWidth: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("কিবোর্ড স্কেলিং ও সাইজ % (Scale Setup)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                // 1. Up-to-Down Height Ratio (Vertical Scale%)
                Text(
                    text = "Up-to-down Height Size%: ${(settings.keyboardHeightRatio * 100).toInt()}%",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Slider(
                    value = settings.keyboardHeightRatio,
                    onValueChange = onUpdateHeightRatio,
                    valueRange = 0.75f..1.35f
                )

                // 2. Side Size Ratio (Horizontal Width%)
                Text(
                    text = "Side Width Size%: ${(settings.keyboardWidthRatio * 100).toInt()}%",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Slider(
                    value = settings.keyboardWidthRatio,
                    onValueChange = onUpdateWidthRatio,
                    valueRange = 0.70f..1.0f
                )

                // 3. Key Font Size Ratio
                Text(
                    text = "Key Font Text Size%: ${(settings.keyFontSizeRatio * 100).toInt()}%",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Slider(
                    value = settings.keyFontSizeRatio,
                    onValueChange = onUpdateFontSizeRatio,
                    valueRange = 0.80f..1.30f
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("বাটন গ্যাপ ও মার্জিন (Gaps & Margins)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                // Key Corner Radius
                Text("বাটন রাউন্ড শেপ (Key Corner Radius): ${settings.keyCornerRadiusDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keyCornerRadiusDp.toFloat(),
                    onValueChange = { onUpdateCornerRadius(it.toInt()) },
                    valueRange = 0f..24f
                )

                // Key Vertical Gap
                Text("বাটনের ওপর-নিচের ফাঁকা (Vertical Gap): ${settings.keyVerticalGapDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keyVerticalGapDp.toFloat(),
                    onValueChange = { onUpdateVerticalGap(it.toInt()) },
                    valueRange = 1f..14f
                )

                // Key Horizontal Gap
                Text("বাটনের পাশের ফাঁকা (Horizontal Gap): ${settings.keyHorizontalGapDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keyHorizontalGapDp.toFloat(),
                    onValueChange = { onUpdateHorizontalGap(it.toInt()) },
                    valueRange = 1f..12f
                )

                // Side Margin
                Text("কিবোর্ডের দুই পাশের মার্জিন (Side Margin): ${settings.keyboardSideMarginDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keyboardSideMarginDp.toFloat(),
                    onValueChange = { onUpdateSideMargin(it.toInt()) },
                    valueRange = 0f..28f
                )

                // Bottom Margin
                Text("কিবোর্ডের নিচের মার্জিন (Bottom Chin Margin): ${settings.keyboardBottomMarginDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keyboardBottomMarginDp.toFloat(),
                    onValueChange = { onUpdateBottomMargin(it.toInt()) },
                    valueRange = 0f..28f
                )

                // 3D Key Elevation
                Text("বাটন 3D শেডো / এলিভেশন (Key 3D Shadow): ${settings.keyElevationDp.toInt()} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keyElevationDp,
                    onValueChange = onUpdateKeyElevation,
                    valueRange = 0f..8f
                )

                // Key Border Width
                Text("বাটন আউটলাইন বর্ডার (Border Width): ${settings.customKeyBorderWidthDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.customKeyBorderWidthDp,
                    onValueChange = onUpdateKeyBorderWidth,
                    valueRange = 0f..4f
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 4: Shortcuts Setup ("Mon Moto Setup" & Suggestion Bar)
// -----------------------------------------------------------------------------------------
@Composable
private fun ShortcutsTabContent(
    settings: KeyboardSettings,
    onUpdateEnabledShortcuts: (Set<String>) -> Unit,
    onUpdateShortcutOrder: (String) -> Unit,
    onUpdateShowSuggestionMic: (Boolean) -> Unit,
    onUpdateShowSuggestionVideo: (Boolean) -> Unit,
    onUpdateShowQuickPaste: (Boolean) -> Unit,
    onUpdateToolbarPosition: (String) -> Unit
) {
    val allAvailableShortcuts = listOf(
        ShortcutDef("clipboard", "📋 Copypad / Clipboard", "কপিকৃত টেক্সট তালিকা ও হিস্টরি", Icons.Default.ContentPaste),
        ShortcutDef("themes", "🎨 Theme Studio", "থিম ও কাস্টম কালার পরিবর্তন", Icons.Default.Palette),
        ShortcutDef("text_edit", "✏️ Text Edit Pad", "সিলেক্ট, কাট, কপি, পেস্ট ও কার্সার জয়স্টিক", Icons.Default.Edit),
        ShortcutDef("numpad", "🔢 Dialer / Numpad", "ফোন ডায়ালপ্যাড ও নাম্বার প্যাড", Icons.Default.Dialpad),
        ShortcutDef("voice", "🎤 Voice Typing", "বাংলা ও ইংরেজি ভয়েস টাইপিং", Icons.Default.Mic),
        ShortcutDef("video_overlay", "🎬 Video Overlay", "কিবোর্ড ব্যাকগ্রাউন্ডে ভিডিও প্লে", Icons.Default.SwitchVideo),
        ShortcutDef("tiktok", "📥 TikTok Downloader", "ওয়াটারমার্ক ছাড়া সোশ্যাল ভিডিও ডাউনলোডার", Icons.Default.Download),
        ShortcutDef("stickers", "✨ Stickers Gallery", "বাংলা ফানি ও রিদমিক স্টিকার", Icons.Default.AutoAwesome),
        ShortcutDef("media", "🎵 In-Keyboard Media", "টাইপিং করার সময় মিউজিক অডিও প্লেয়ার", Icons.Default.Audiotrack),
        ShortcutDef("vault", "🔐 Password Vault", "অটো সেভড পাসওয়ার্ড ও সিকিউর অটোফিল", Icons.Default.Lock),
        ShortcutDef("language", "🌐 Language Switcher", "English, বাংলা ও অভ্র সাইকেল", Icons.Default.Language),
        ShortcutDef("vibration", "📳 Vibration Toggle", "ক্লিক ভাইব্রেশন চালু/বন্ধ", Icons.Default.Vibration),
        ShortcutDef("sound", "🔊 Sound Toggle", "বাটন সাউন্ড চালু/বন্ধ", Icons.Default.VolumeUp),
        ShortcutDef("one_handed", "📱 One-Handed Mode", "এক হাতে টাইপ করার আর্ক মোড", Icons.Default.AspectRatio),
        ShortcutDef("settings", "⚙️ Keyboard Settings", "মেইন অ্যাপ ও ফুল সেটিংস পেজ", Icons.Default.Settings)
    )

    val currentOrderList = remember(settings.shortcutOrder) {
        val list = settings.shortcutOrder.split(",").filter { it.isNotBlank() }.toMutableList()
        allAvailableShortcuts.forEach { def ->
            if (!list.contains(def.id)) {
                list.add(def.id)
            }
        }
        list
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Suggestion Bar Quick Controls
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("সাজেশন বারের আইকন কনফিগার (Suggestion Bar Icons)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎤 ভয়েস মাইক আইকন দেখান (Voice Mic Icon)", fontSize = 13.sp)
                    Switch(checked = settings.showSuggestionMicIcon, onCheckedChange = onUpdateShowSuggestionMic)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎬 ভিডিও প্লে/পজ আইকন দেখান (Video Overlay Icon)", fontSize = 13.sp)
                    Switch(checked = settings.showSuggestionVideoIcon, onCheckedChange = onUpdateShowSuggestionVideo)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📋 কুইক পেস্ট চিপ দেখান (Quick Paste Chip)", fontSize = 13.sp)
                    Switch(checked = settings.showQuickPasteChip, onCheckedChange = onUpdateShowQuickPaste)
                }
            }
        }

        // Shortcut List Configuration ("Mon Moto Setup")
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("শর্টকাট বাটন মন মতো সাজান (Reorder & Toggle Shortcuts)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "যে শর্টকাটগুলো টুলবারে চান সেগুলো চালু রাখুন এবং ওপর-নিচে সাজিয়ে প্রথম দিকে রাখুন।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                currentOrderList.forEachIndexed { index, shortcutId ->
                    val def = allAvailableShortcuts.find { it.id == shortcutId }
                    if (def != null) {
                        val isEnabled = settings.enabledShortcuts.contains(shortcutId)

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = def.icon,
                                        contentDescription = null,
                                        tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(def.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(def.subtitle, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Move Up Button
                                    IconButton(
                                        onClick = {
                                            if (index > 0) {
                                                val newList = currentOrderList.toMutableList()
                                                val item = newList.removeAt(index)
                                                newList.add(index - 1, item)
                                                onUpdateShortcutOrder(newList.joinToString(","))
                                            }
                                        },
                                        enabled = index > 0,
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", modifier = Modifier.size(18.dp))
                                    }

                                    // Move Down Button
                                    IconButton(
                                        onClick = {
                                            if (index < currentOrderList.size - 1) {
                                                val newList = currentOrderList.toMutableList()
                                                val item = newList.removeAt(index)
                                                newList.add(index + 1, item)
                                                onUpdateShortcutOrder(newList.joinToString(","))
                                            }
                                        },
                                        enabled = index < currentOrderList.size - 1,
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", modifier = Modifier.size(18.dp))
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    // Toggle Switch
                                    Switch(
                                        checked = isEnabled,
                                        onCheckedChange = { checked ->
                                            val newSet = settings.enabledShortcuts.toMutableSet()
                                            if (checked) newSet.add(shortcutId) else newSet.remove(shortcutId)
                                            onUpdateEnabledShortcuts(newSet)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ShortcutDef(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

// -----------------------------------------------------------------------------------------
// Tab 5: Theme Icons Style
// -----------------------------------------------------------------------------------------
@Composable
private fun ThemeIconsTabContent(
    settings: KeyboardSettings,
    onUpdateIconStyle: (String) -> Unit
) {
    val iconStyles = listOf(
        IconStyleOption("standard", "Standard Minimal", "ক্লিন ও স্ট্যান্ডার্ড অ্যান্ড্রয়েড আইকন স্টাইল", "🔘"),
        IconStyleOption("retro_mech", "3D Retro Mech", "রেট্রো মেকানিক্যাল কিবোর্ড আর্টওয়ার্ক", "⌨️"),
        IconStyleOption("kawaii_kitten", "Kawaii Kitten", "মিষ্টি ও কিউট বিড়াল অ্যানিমে আইকন", "🐱"),
        IconStyleOption("puppy_pop", "Puppy Pop", "হোয়াইট পাপি কিউট থিম আইকন", "🐶"),
        IconStyleOption("strawberry", "Strawberry Dessert", "স্ট্রবেরি ডেজার্ট প্যাস্টেল মিষ্টি আইকন", "🍓"),
        IconStyleOption("rgb", "RGB Chroma Neon", "নিয়ন গেমিং ব্যাকলিট ভাইব্র্যান্ট আইকন", "🌈"),
        IconStyleOption("cat", "3D Slate Cat", "3D ডার্ক স্লেট ক্যাট কিবোর্ড আইকন", "🐾"),
        IconStyleOption("minimal", "Reference Minimal", "প্রো মিনিমাল আউটলাইন আইকন", "✨")
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("থিমের বিশেষ আইকন স্টাইল (Theme Icon Artwork)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "স্পেসবার, শিফট ও এন্টার বাটনে আপনার পছন্দের ক্যারেক্টার বা মিনিমাল আইকন সেট করুন।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                iconStyles.forEach { style ->
                    val isSelected = settings.customThemeIconStyle.lowercase() == style.id.lowercase()

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdateIconStyle(style.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(style.emoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(style.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(style.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class IconStyleOption(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String
)
