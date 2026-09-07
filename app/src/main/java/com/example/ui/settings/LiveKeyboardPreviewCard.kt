package com.example.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.KeyboardSettings
import com.example.language.avro.AvroPhoneticEngine
import com.example.theme.KeyboardPalette
import com.example.theme.KeyboardThemes
import java.util.Locale

@Composable
fun LiveKeyboardPreviewCard(
    settings: KeyboardSettings,
    onSelectTheme: (String) -> Unit = {},
    onUpdateUiMode: (String) -> Unit = {},
    onOpenThemes: () -> Unit = {}
) {
    var previewLanguage by remember { mutableStateOf("avro") }
    var typedText by remember { mutableStateOf("") }
    var isShifted by remember { mutableStateOf(false) }
    val avroEngine = remember { AvroPhoneticEngine() }
    val activePalette = KeyboardThemes.getPalette(settings.theme)
    val isRidmikMode = settings.uiMode == "original" || settings.theme.startsWith("ridmik")

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_live_keyboard_preview")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "লাইভ কিবোর্ড প্রিভিউ (Live Preview)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "সরাসরি কিবোর্ড ছুঁয়ে টাইপিং টেস্ট করুন",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenThemes,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("থিম", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Output / Sandbox Screen Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (typedText.isEmpty()) Color.Gray else Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (previewLanguage == "avro") "অভ্র ফোনেটিক (Avro)" else if (previewLanguage == "bangla") "জাতীয় বাংলা (Bangla)" else "ইংরেজি (English)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (typedText.isNotEmpty()) {
                            Text(
                                text = "Clear",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { typedText = "" }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (typedText.isEmpty()) "নিচের কিবোর্ড বাটনে চাপ দিয়ে টাইপ করুন..." else typedText,
                        fontSize = 15.sp,
                        fontWeight = if (typedText.isEmpty()) FontWeight.Normal else FontWeight.Medium,
                        color = if (typedText.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                        minLines = 2,
                        maxLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Language & Mode Switcher Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = previewLanguage == "avro",
                    onClick = { previewLanguage = "avro" },
                    label = { Text("Avro ফোনেটিক", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                )

                FilterChip(
                    selected = previewLanguage == "english",
                    onClick = { previewLanguage = "english" },
                    label = { Text("English", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                )

                FilterChip(
                    selected = previewLanguage == "bangla",
                    onClick = { previewLanguage = "bangla" },
                    label = { Text("জাতীয় (Bangla)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Layout style switch (Ridmik vs NXV)
                FilterChip(
                    selected = isRidmikMode,
                    onClick = {
                        onUpdateUiMode(if (isRidmikMode) "modern" else "original")
                    },
                    label = {
                        Text(
                            text = if (isRidmikMode) "💎 Ridmik Layout" else "⚡ NXV Layout",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRidmikMode) Color(0xFF1565C0) else MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Embedded Live Visual Keyboard Canvas
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, activePalette.keyBorderColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                color = activePalette.keyboardBackground
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Suggestions Top Strip inside Preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(activePalette.suggestionBarBackground),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val sampleSuggestions = when (previewLanguage) {
                            "avro" -> listOf("আমি", "আমার", "আমাদের", "বাংলায়")
                            "bangla" -> listOf("ধন্যবাদ", "কেমন", "আছেন", "সুন্দর")
                            else -> listOf("the", "and", "hello", "keyboard")
                        }
                        for (sug in sampleSuggestions) {
                            Text(
                                text = sug,
                                color = activePalette.textColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        typedText += if (typedText.isEmpty()) sug else " $sug"
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Keyboard Rows
                    if (previewLanguage == "bangla") {
                        // Bangla National layout preview
                        val row1 = listOf("১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯", "০")
                        val row2 = if (isShifted) listOf("ৌ", "ৈ", "া", "ী", "ূ", "ভ", "ঙ", "ঘ", "ধ", "ঝ") else listOf("ো", "ে", "া", "ি", "ু", "ব", "হ", "গ", "দ", "জ")
                        val row3 = if (isShifted) listOf("ঋ", "য", "শ", "ষ", "ণ", "খ", "থ", "ছ", "ঠ", "ফ") else listOf("ৃ", "র", "স", "ট", "ন", "ক", "ত", "চ", "ট", "প")
                        val row4 = if (isShifted) listOf("অ", "আ", "ই", "ঈ", "উ", "ঊ", "এ", "ঐ", "ও", "ঔ") else listOf("ং", "ঃ", "ঁ", "্", "ম", "ন", "ল", "স", "হ", "ড়")

                        LivePreviewKeyRow(keys = row1, palette = activePalette) { char -> typedText += char }
                        LivePreviewKeyRow(keys = row2, palette = activePalette) { char -> typedText += char }
                        LivePreviewKeyRow(keys = row3, palette = activePalette) { char -> typedText += char }
                        LivePreviewKeyRow(keys = row4, palette = activePalette) { char -> typedText += char }
                    } else {
                        // English & Avro layout preview
                        val row1 = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
                        val row2 = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
                        val row3 = listOf("Z", "X", "C", "V", "B", "N", "M")

                        LivePreviewKeyRow(keys = row1, palette = activePalette, isShifted = isShifted) { char ->
                            handlePreviewCharInput(char, isShifted, previewLanguage, avroEngine, typedText) { updated ->
                                typedText = updated
                            }
                        }
                        LivePreviewKeyRow(keys = row2, palette = activePalette, isShifted = isShifted) { char ->
                            handlePreviewCharInput(char, isShifted, previewLanguage, avroEngine, typedText) { updated ->
                                typedText = updated
                            }
                        }

                        // Row 3 with Shift and Backspace
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Shift Key
                            LivePreviewSpecialKey(
                                text = if (isShifted) "▲" else "⇧",
                                modifier = Modifier.weight(1.3f),
                                palette = activePalette,
                                isAccent = isShifted
                            ) {
                                isShifted = !isShifted
                            }

                            // Letters in row 3
                            for (char in row3) {
                                val label = if (isShifted) char else char.lowercase(Locale.US)
                                LivePreviewKey(
                                    label = label,
                                    modifier = Modifier.weight(1f),
                                    palette = activePalette
                                ) {
                                    handlePreviewCharInput(label, isShifted, previewLanguage, avroEngine, typedText) { updated ->
                                        typedText = updated
                                    }
                                }
                            }

                            // Backspace Key
                            LivePreviewSpecialKey(
                                text = "⌫",
                                modifier = Modifier.weight(1.3f),
                                palette = activePalette
                            ) {
                                if (typedText.isNotEmpty()) {
                                    typedText = typedText.substring(0, typedText.length - 1)
                                }
                            }
                        }
                    }

                    // Bottom Row (123, Spacebar, Enter)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LivePreviewSpecialKey(
                            text = "?123",
                            modifier = Modifier.weight(1.4f),
                            palette = activePalette
                        ) {
                            typedText += "1"
                        }

                        LivePreviewSpecialKey(
                            text = if (previewLanguage == "avro") "বাংলা" else "English",
                            modifier = Modifier.weight(1.2f),
                            palette = activePalette
                        ) {
                            previewLanguage = if (previewLanguage == "avro") "english" else "avro"
                        }

                        // Spacebar
                        Box(
                            modifier = Modifier
                                .weight(3.5f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(activePalette.keyBackground)
                                .border(0.8.dp, activePalette.keyBorderColor, RoundedCornerShape(6.dp))
                                .clickable {
                                    if (previewLanguage == "avro") {
                                        val words = typedText.split(" ")
                                        if (words.isNotEmpty()) {
                                            val last = words.last()
                                            val conv = avroEngine.convertWord(last)
                                            typedText = if (words.size > 1) {
                                                words.dropLast(1).joinToString(" ") + " " + conv + " "
                                            } else {
                                                conv + " "
                                            }
                                        } else {
                                            typedText += " "
                                        }
                                    } else {
                                        typedText += " "
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Space (স্পেস)",
                                color = activePalette.textColor.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }

                        LivePreviewSpecialKey(
                            text = "↵",
                            modifier = Modifier.weight(1.4f),
                            palette = activePalette,
                            isAccent = true
                        ) {
                            typedText += "\n"
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Theme Palette Carousel underneath
            Text(
                text = "জনপ্রিয় থিমসমূহ (Quick Themes):",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val featuredThemes = listOf(
                    "ridmik_dark" to "Ridmik Dark",
                    "ridmik_probhat" to "Ridmik Probhat",
                    "ridmik_white" to "Ridmik White",
                    "nxv_enhanced" to "NXV Modern",
                    "retro_mech" to "Retro Mech",
                    "puppy_pop" to "Puppy Pop",
                    "strawberry_dessert" to "Strawberry",
                    "amoled" to "AMOLED Black"
                )
                for ((id, name) in featuredThemes) {
                    val pal = KeyboardThemes.getPalette(id)
                    val isSelected = settings.theme == id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectTheme(id) },
                        color = pal.keyboardBackground,
                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else pal.keyBorderColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(pal.accentColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = pal.textColor
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun handlePreviewCharInput(
    char: String,
    isShifted: Boolean,
    mode: String,
    avroEngine: AvroPhoneticEngine,
    currentText: String,
    onUpdate: (String) -> Unit
) {
    val charToType = if (isShifted) char.uppercase(Locale.US) else char.lowercase(Locale.US)
    if (mode == "avro") {
        val next = currentText + charToType
        onUpdate(next)
    } else {
        onUpdate(currentText + charToType)
    }
}

@Composable
private fun LivePreviewKeyRow(
    keys: List<String>,
    palette: KeyboardPalette,
    isShifted: Boolean = false,
    onKeyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (key in keys) {
            val label = if (isShifted) key.uppercase(Locale.US) else key.lowercase(Locale.US)
            LivePreviewKey(
                label = label,
                modifier = Modifier.weight(1f),
                palette = palette,
                onClick = { onKeyClick(label) }
            )
        }
    }
}

@Composable
private fun LivePreviewKey(
    label: String,
    modifier: Modifier,
    palette: KeyboardPalette,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(palette.keyBackground)
            .border(0.8.dp, palette.keyBorderColor, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = palette.textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LivePreviewSpecialKey(
    text: String,
    modifier: Modifier,
    palette: KeyboardPalette,
    isAccent: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isAccent) palette.accentColor else palette.keyActionBackground)
            .border(0.8.dp, palette.keyBorderColor, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isAccent) palette.onAccentColor else palette.textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
