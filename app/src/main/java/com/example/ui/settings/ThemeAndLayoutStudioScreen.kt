package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.preferences.KeyboardSettings
import com.example.theme.KeyboardPalette
import com.example.theme.KeyboardThemes
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ThemeAndLayoutStudioScreen(
    initialTab: Int = 0,
    settings: KeyboardSettings,
    onSelectTheme: (String) -> Unit,
    onUpdateUiMode: (String) -> Unit,
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
    onUpdateCustomKeyBorderWidthDp: (Float) -> Unit,
    onUpdateCustomKeyBorderColor: (Long) -> Unit,
    onUpdateKeyElevationDp: (Float) -> Unit,
    onUpdateKeyCornerRadiusDp: (Int) -> Unit,
    onUpdateKeyboardHeightRatio: (Float) -> Unit,
    onUpdateShowNumberRow: (Boolean) -> Unit,
    onUpdateShowEmojiKey: (Boolean) -> Unit,
    onUpdateShowLanguageKey: (Boolean) -> Unit,
    onUpdateShowKeySubLabels: (Boolean) -> Unit,
    onUpdateKeyPopupMode: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab.coerceIn(0, 3)) }
    val isRidmikMode = settings.uiMode == "original" || settings.theme.startsWith("ridmik")
    val activePalette = remember(settings.theme, settings.customThemeEnabled, settings.customKeyBgColor, settings.customAccentColor) {
        KeyboardThemes.getPalette(settings.theme, settings)
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdateCustomBackgroundImageUri(uri.toString())
            onUpdateCustomThemeEnabled(true)
        }
    }

    val tabTitles = listOf(
        "🎨 থিম গ্যালারি",
        "🖼️ ফটো ও ওয়ালপেপার",
        "🌈 কালার ও কিবোর্ড কী",
        "📐 সাইজ ও বাটন"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "থিম ও লেআউট স্টুডিও",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (settings.customThemeEnabled) "সক্রিয়: কাস্টম থিম" else "সক্রিয়: ${activePalette.themeName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_theme_studio")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পিছনে")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSelectTheme("ridmik_dark")
                            onUpdateCustomThemeEnabled(false)
                            onUpdateCustomBackgroundImageUri("")
                            onUpdateKeyboardHeightRatio(1.0f)
                            onUpdateShowNumberRow(false)
                            onUpdateShowEmojiKey(true)
                            onUpdateShowLanguageKey(true)
                            onUpdateShowKeySubLabels(true)
                            onUpdateKeyPopupMode("popup")
                            onUpdateKeyCornerRadiusDp(8)
                            onUpdateKeyElevationDp(1.5f)
                            onUpdateCustomKeyBorderWidthDp(0.5f)
                        },
                        modifier = Modifier.testTag("btn_reset_theme_studio")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "ডিফল্ট রিসেট", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Header Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().testTag("tabs_theme_studio")
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Live Interactive Keyboard Preview Card
                LiveKeyboardPreviewCard(
                    settings = settings,
                    onSelectTheme = { themeId ->
                        onSelectTheme(themeId)
                        onUpdateCustomThemeEnabled(false)
                    },
                    onUpdateUiMode = onUpdateUiMode,
                    onOpenThemes = { selectedTab = 0 }
                )

                // Tab Specific Contents
                when (selectedTab) {
                    0 -> ThemeGalleryTab(
                        settings = settings,
                        activePalette = activePalette,
                        isRidmikMode = isRidmikMode,
                        onSelectTheme = { themeId ->
                            onSelectTheme(themeId)
                            onUpdateCustomThemeEnabled(false)
                        },
                        onUpdateUiMode = onUpdateUiMode
                    )

                    1 -> PhotoWallpaperTab(
                        settings = settings,
                        onPickPhoto = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRemovePhoto = {
                            onUpdateCustomBackgroundImageUri("")
                        },
                        onUpdateDim = onUpdateCustomBackgroundDim,
                        onUpdateBlur = onUpdateCustomBackgroundBlur,
                        onApplyGradientPreset = { bg, key, action, text, accent ->
                            onUpdateCustomKeyboardBg(bg)
                            onUpdateCustomKeyBg(key)
                            onUpdateCustomKeyActionBg(action)
                            onUpdateCustomTextColor(text)
                            onUpdateCustomAccentColor(accent)
                            onUpdateCustomThemeEnabled(true)
                        }
                    )

                    2 -> ColorsAndKeyStylingTab(
                        settings = settings,
                        onUpdateCustomThemeEnabled = onUpdateCustomThemeEnabled,
                        onUpdateCustomKeyboardBg = onUpdateCustomKeyboardBg,
                        onUpdateCustomKeyBg = onUpdateCustomKeyBg,
                        onUpdateCustomTextColor = onUpdateCustomTextColor,
                        onUpdateCustomAccentColor = onUpdateCustomAccentColor,
                        onUpdateKeyBorderWidth = onUpdateCustomKeyBorderWidthDp,
                        onUpdateKeyBorderColor = onUpdateCustomKeyBorderColor,
                        onUpdateCornerRadius = onUpdateKeyCornerRadiusDp,
                        onUpdateElevation = onUpdateKeyElevationDp
                    )

                    3 -> SizeAndButtonsTab(
                        settings = settings,
                        onUpdateHeightRatio = onUpdateKeyboardHeightRatio,
                        onUpdateShowNumberRow = onUpdateShowNumberRow,
                        onUpdateShowEmojiKey = onUpdateShowEmojiKey,
                        onUpdateShowLanguageKey = onUpdateShowLanguageKey,
                        onUpdateShowKeySubLabels = onUpdateShowKeySubLabels,
                        onUpdateKeyPopupMode = onUpdateKeyPopupMode
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 0: THEME GALLERY (রেডিমেড থিমস)
// -----------------------------------------------------------------------------
@Composable
private fun ThemeGalleryTab(
    settings: KeyboardSettings,
    activePalette: KeyboardPalette,
    isRidmikMode: Boolean,
    onSelectTheme: (String) -> Unit,
    onUpdateUiMode: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("সব (All)") }
    val categories = listOf("সব (All)", "বাংলা ও রিদমিক", "ডার্ক ও AMOLED", "গেমিং ও স্পেশাল", "লাইট ও ক্লিন")

    val allThemes = KeyboardThemes.ALL_THEMES
    val filteredThemes = remember(selectedCategory) {
        when (selectedCategory) {
            "বাংলা ও রিদমিক" -> allThemes.filter { it.themeId in listOf("ridmik_dark", "ridmik_probhat", "ridmik_classic", "ridmik_white") }
            "ডার্ক ও AMOLED" -> allThemes.filter { it.themeId in listOf("amoled", "freefire_black_gold", "ridmik_dark", "ridmik_probhat", "nxv_enhanced", "retro_mech", "cat_3d_slate") }
            "গেমিং ও স্পেশাল" -> allThemes.filter { it.themeId in listOf("freefire_black_gold", "rgb_neon", "retro_mech", "cat_3d_slate", "kawaii_kitten", "puppy_pop", "strawberry_dessert") }
            "লাইট ও ক্লিন" -> allThemes.filter { it.themeId in listOf("ridmik_white", "light", "reference_minimal", "geometric") }
            else -> allThemes
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Layout Mode Switch Card (Ridmik vs NXV)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isRidmikMode) Color(0xFF0A84FF).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRidmikMode) Icons.Default.Layers else Icons.Default.Tune,
                            contentDescription = null,
                            tint = if (isRidmikMode) Color(0xFF0A84FF) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isRidmikMode) "Ridmik Original Layout" else "NXV Modern Layout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isRidmikMode) "ক্লাসিক রিদমিক কিবোর্ড লেআউট সক্রিয়" else "মডার্ন এজ-টু-এজ কী প্যাটার্ন সক্রিয়",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Button(
                    onClick = { onUpdateUiMode(if (isRidmikMode) "modern" else "original") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRidmikMode) Color(0xFF0A84FF) else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isRidmikMode) "সুইচ: NXV" else "সুইচ: Ridmik", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // Themes List / Cards
        filteredThemes.forEach { palette ->
            val isSelected = !settings.customThemeEnabled && settings.theme == palette.themeId
            ThemeItemCard(
                palette = palette,
                isSelected = isSelected,
                onApply = { onSelectTheme(palette.themeId) }
            )
        }
    }
}

@Composable
private fun ThemeItemCard(
    palette: KeyboardPalette,
    isSelected: Boolean,
    onApply: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onApply() }
            .testTag("card_theme_${palette.themeId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Theme Visual Swatches & Details
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Mini 4-color palette swatch preview
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.keyboardBackground)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(modifier = Modifier.size(15.dp).clip(RoundedCornerShape(4.dp)).background(palette.keyBackground))
                            Box(modifier = Modifier.size(15.dp).clip(RoundedCornerShape(4.dp)).background(palette.accentColor))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(modifier = Modifier.size(15.dp).clip(RoundedCornerShape(4.dp)).background(palette.keyActionBackground))
                            Box(modifier = Modifier.size(15.dp).clip(RoundedCornerShape(4.dp)).background(palette.textColor))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = palette.themeName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("সক্রিয়", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        text = palette.themeDescription,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Button
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "সক্রিয়",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            } else {
                OutlinedButton(
                    onClick = onApply,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("ব্যবহার করুন", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 1: PHOTO & WALLPAPER (ফটো ও ব্যাকগ্রাউন্ড)
// -----------------------------------------------------------------------------
@Composable
private fun PhotoWallpaperTab(
    settings: KeyboardSettings,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onUpdateDim: (Float) -> Unit,
    onUpdateBlur: (Float) -> Unit,
    onApplyGradientPreset: (Long, Long, Long, Long, Long) -> Unit
) {
    val hasCustomPhoto = settings.customBackgroundImageUri.isNotBlank()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Photo Picker Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("নিজের ছবি সেট করুন (Custom Photo)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = if (hasCustomPhoto) "কিবোর্ড ব্যাকগ্রাউন্ডে ফটো সেট করা আছে" else "গ্যালারি থেকে ফটো নির্বাচন করুন",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (hasCustomPhoto) {
                    // Photo Preview Thumbnail + Remove Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = settings.customBackgroundImageUri,
                            contentDescription = "Background Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Dim overlay preview
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = settings.customBackgroundDim))
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onPickPhoto,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("পরিবর্তন", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onRemovePhoto,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("মুছুন", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Sliders for Photo Tuning
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ডার্কনেস ও লেখা পড়ার সুবিধা (Dim)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("${(settings.customBackgroundDim * 100).roundToInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = settings.customBackgroundDim,
                            onValueChange = onUpdateDim,
                            valueRange = 0.1f..0.85f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ব্লার ইফেক্ট (Frosted Blur)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("${settings.customBackgroundBlur.roundToInt()} dp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = settings.customBackgroundBlur,
                            onValueChange = onUpdateBlur,
                            valueRange = 0f..20f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Button(
                        onClick = onPickPhoto,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_pick_photo")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("গ্যালারি থেকে ফটো বাছাই করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Wallpaper Color Gradients
        Text("অথবা রেডিমেড গ্রেডিয়েন্ট প্যালেট বাছুন:", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        val gradientPresets = listOf(
            GradientPresetItem("সাইবার ব্লু", 0xFF0D1B2A, 0xFF1B263B, 0xFF0D1B2A, 0xFFE0E1DD, 0xFF00D2FF),
            GradientPresetItem("মিডনাইট পার্পল", 0xFF190926, 0xFF2D1445, 0xFF200C33, 0xFFF3E8FF, 0xFFA855F7),
            GradientPresetItem("রোজ ভেলভেট", 0xFF240E17, 0xFF3D1626, 0xFF2B0E1B, 0xFFFFE4E6, 0xFFF43F5E),
            GradientPresetItem("ফরেস্ট এমারেল্ড", 0xFF081C15, 0xFF1B4332, 0xFF102820, 0xFFD8F3DC, 0xFF10B981),
            GradientPresetItem("গোল্ডেন অ্যাম্বার", 0xFF1C1304, 0xFF362409, 0xFF261906, 0xFFFEF3C7, 0xFFF59E0B),
            GradientPresetItem("পিউর ব্ল্যাক ও হোয়াইট", 0xFF000000, 0xFF1C1C1E, 0xFF121214, 0xFFFFFFFF, 0xFF0A84FF)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            gradientPresets.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(preset.bg),
                            border = BorderStroke(1.dp, Color(preset.accent).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clickable {
                                    onApplyGradientPreset(preset.bg, preset.key, preset.action, preset.text, preset.accent)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = preset.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(preset.text)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(preset.accent))
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private data class GradientPresetItem(
    val name: String,
    val bg: Long,
    val key: Long,
    val action: Long,
    val text: Long,
    val accent: Long
)

// -----------------------------------------------------------------------------
// TAB 2: COLORS & KEY STYLING (কালার ও কিবোর্ড কী)
// -----------------------------------------------------------------------------
@Composable
private fun ColorsAndKeyStylingTab(
    settings: KeyboardSettings,
    onUpdateCustomThemeEnabled: (Boolean) -> Unit,
    onUpdateCustomKeyboardBg: (Long) -> Unit,
    onUpdateCustomKeyBg: (Long) -> Unit,
    onUpdateCustomTextColor: (Long) -> Unit,
    onUpdateCustomAccentColor: (Long) -> Unit,
    onUpdateKeyBorderWidth: (Float) -> Unit,
    onUpdateKeyBorderColor: (Long) -> Unit,
    onUpdateCornerRadius: (Int) -> Unit,
    onUpdateElevation: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Master Custom Theme Switch
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (settings.customThemeEnabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = BorderStroke(1.dp, if (settings.customThemeEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
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
                    Text("কাস্টম কালার মোড (Custom Color Mode)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = if (settings.customThemeEnabled) "আপনার পছন্দসই কালার কিবোর্ডে কার্যকর" else "রেডিমেড থিম বন্ধ করে নিজের কালার প্রয়োগ করুন",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.customThemeEnabled,
                    onCheckedChange = onUpdateCustomThemeEnabled,
                    modifier = Modifier.testTag("switch_custom_theme")
                )
            }
        }

        // Key Background Color Section
        ColorPickerRow(
            title = "বাটন ব্যাকগ্রাউন্ড (Key Background)",
            currentColor = settings.customKeyBgColor,
            options = listOf(0xFF20232A, 0xFF121316, 0xFF2A2D34, 0xFF000000, 0xFF2D3748, 0xFFE5E7EB, 0xFF1E293B),
            onSelect = {
                onUpdateCustomKeyBg(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        // Key Text Color Section
        ColorPickerRow(
            title = "লেখার রং (Key Text Color)",
            currentColor = settings.customTextColor,
            options = listOf(0xFFFFFFFF, 0xFFF0F4F8, 0xFF111827, 0xFFFFD700, 0xFF00D2FF, 0xFF6EE7B7),
            onSelect = {
                onUpdateCustomTextColor(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        // Accent Color Section (Enter & Special)
        ColorPickerRow(
            title = "অ্যাকসেন্ট রং (Enter / Special Key)",
            currentColor = settings.customAccentColor,
            options = listOf(0xFF00D2FF, 0xFF0A84FF, 0xFFFFD700, 0xFF10B981, 0xFFF43F5E, 0xFFA855F7, 0xFFF59E0B),
            onSelect = {
                onUpdateCustomAccentColor(it)
                onUpdateCustomThemeEnabled(true)
            }
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        // Key Shape & Geometry
        Text("কী-এর আকৃতি ও শ্যাডো (Key Geometry)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        // Corner Radius Slider
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("বাটন রাউন্ডনেস (Corner Radius)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("${settings.keyCornerRadiusDp} dp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = settings.keyCornerRadiusDp.toFloat(),
                    onValueChange = {
                        onUpdateCornerRadius(it.roundToInt())
                        onUpdateCustomThemeEnabled(true)
                    },
                    valueRange = 2f..16f,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("বক্স / স্কয়ার (Square)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("গোলাকার (Pill Round)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // 3D Key Elevation Slider
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("কী-এর ৩ডি শ্যাডো / উচ্চতা (3D Key Elevation)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(String.format("%.1f dp", settings.keyElevationDp), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = settings.keyElevationDp,
                    onValueChange = {
                        onUpdateElevation(it)
                        onUpdateCustomThemeEnabled(true)
                    },
                    valueRange = 0f..4f,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ফ্ল্যাট (Flat 0dp)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("উঁচু ৩ডি (3D Depth 4dp)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Key Border Thickness & Color
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("বাটন বর্ডার বা লাইন (Key Border)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(String.format("%.1f dp", settings.customKeyBorderWidthDp), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = settings.customKeyBorderWidthDp,
                    onValueChange = {
                        onUpdateKeyBorderWidth(it)
                        onUpdateCustomThemeEnabled(true)
                    },
                    valueRange = 0f..3f,
                    modifier = Modifier.fillMaxWidth()
                )
                // Border Color Swatches
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("বর্ডার কালার:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    listOf(0x33FFFFFF, 0x80FFFFFF, 0xFF00D2FF, 0xFFFFD700, 0x40000000).forEach { colorVal ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(1.dp, if (settings.customKeyBorderColor == colorVal) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
                                .clickable {
                                    onUpdateKeyBorderColor(colorVal)
                                    onUpdateCustomThemeEnabled(true)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPickerRow(
    title: String,
    currentColor: Long,
    options: List<Long>,
    onSelect: (Long) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(currentColor))
                        .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { colorVal ->
                    val isSelected = currentColor == colorVal
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(colorVal))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                            .clickable { onSelect(colorVal) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (Color(colorVal).red > 0.6f && Color(colorVal).green > 0.6f) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 3: SIZE & BUTTON LAYOUT (সাইজ ও বাটন)
// -----------------------------------------------------------------------------
@Composable
private fun SizeAndButtonsTab(
    settings: KeyboardSettings,
    onUpdateHeightRatio: (Float) -> Unit,
    onUpdateShowNumberRow: (Boolean) -> Unit,
    onUpdateShowEmojiKey: (Boolean) -> Unit,
    onUpdateShowLanguageKey: (Boolean) -> Unit,
    onUpdateShowKeySubLabels: (Boolean) -> Unit,
    onUpdateKeyPopupMode: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Keyboard Height Ratio Slider
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("কিবোর্ডের উচ্চতা (Keyboard Height)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "আঙুলের সুবিধার জন্য কিবোর্ডের সাইজ বড় বা ছোট করুন",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${(settings.keyboardHeightRatio * 100).roundToInt()}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = settings.keyboardHeightRatio,
                    onValueChange = onUpdateHeightRatio,
                    valueRange = 0.8f..1.30f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth().testTag("slider_height_ratio")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("কমপ্যাক্ট (80%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("স্বাভাবিক (100%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("উঁচু / বড় (130%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Functional Button Toggles
        Text("বাটন ও রো সেটিংস (Buttons & Rows)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Number row toggle
                SettingToggleRow(
                    title = "সংখ্যার সারি (Number Row)",
                    subtitle = "কিবোর্ডের শীর্ষে আলাদা ১-০ নম্বরের লাইন দেখাবে",
                    checked = settings.showNumberRow,
                    onCheckedChange = onUpdateShowNumberRow,
                    tag = "toggle_number_row"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Emoji key toggle
                SettingToggleRow(
                    title = "ইমোজি বাটন (Emoji Key 😊)",
                    subtitle = "স্পেসবারের পাশে ইমোজি প্যানেল খোলার ডেডিকেটেড বাটন",
                    checked = settings.showEmojiKey,
                    onCheckedChange = onUpdateShowEmojiKey,
                    tag = "toggle_emoji_key"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Language switch key toggle
                SettingToggleRow(
                    title = "ভাষা সুইচ বাটন (Language Key 🌐)",
                    subtitle = "স্পেসবারে সোয়াইপ ছাড়াও ভাষা পরিবর্তনের বোতাম",
                    checked = settings.showLanguageKey,
                    onCheckedChange = onUpdateShowLanguageKey,
                    tag = "toggle_lang_key"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Key sub-labels toggle
                SettingToggleRow(
                    title = "কী-এর সাব-লেবেল (Sub-labels & Hints)",
                    subtitle = "লং-প্রেস বিকল্প অক্ষর ও সংখ্যা বাটনের কোণায় দেখাবে",
                    checked = settings.showKeySubLabels,
                    onCheckedChange = onUpdateShowKeySubLabels,
                    tag = "toggle_sublabels"
                )
            }
        }

        // Key Press Popup Mode
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("কী প্রেস পপআপ স্টাইল (Key Popup Style)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("বাটনে চাপলে আঙুলের উপরে বড় করে অক্ষর দেখানোর ধরন", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                listOf(
                    Triple("popup", "ফ্লোটিং বাবল (Modern Floating Bubble)", "আঙুলের উপরে সুন্দরভাবে ভেসে ওঠে"),
                    Triple("mini", "ক্লাসিক টুলটিপ (Classic Tooltip)", "সহজ ও হালকা ছোট পপআপ"),
                    Triple("off", "পপআপ বন্ধ (Off)", "কোনো পপআপ দেখাবে না, দ্রুত টাইপিং")
                ).forEach { (mode, label, desc) ->
                    val isSelected = settings.keyPopupMode == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onUpdateKeyPopupMode(mode) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onUpdateKeyPopupMode(mode) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag)
        )
    }
}
