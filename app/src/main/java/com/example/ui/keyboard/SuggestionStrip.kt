package com.example.ui.keyboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ClipboardItem
import com.example.suggestion.SuggestionItem
import com.example.theme.KeyboardPalette

data class AutoSavePromptData(
    val serviceName: String,
    val username: String,
    val password: String,
    val onConfirmSave: () -> Unit,
    val onDismiss: () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuggestionStrip(
    suggestions: List<SuggestionItem>,
    palette: KeyboardPalette,
    currentLanguage: String = "english",
    autoSavePrompt: AutoSavePromptData? = null,
    recentClip: ClipboardItem? = null,
    clipboardCount: Int = 0,
    onSuggestionClick: (SuggestionItem) -> Unit,
    onClipboardClick: () -> Unit,
    onQuickPaste: ((String) -> Unit)? = null,
    onVaultClick: () -> Unit = {},
    onSettingsClick: () -> Unit,
    onLanguageCycle: () -> Unit = {},
    onLanguageLongPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.suggestionBarBackground)
    ) {
        if (autoSavePrompt != null) {
            // Interactive Auto-Save Banner for Passwords
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "🔐",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save pass for ${autoSavePrompt.serviceName}?",
                        color = palette.textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF4CAF50))
                            .clickable { autoSavePrompt.onConfirmSave() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("autosave_confirm_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Save",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { autoSavePrompt.onDismiss() }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("autosave_dismiss_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            color = palette.secondaryTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Toolbar icons (Clipboard, Vault)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (clipboardCount > 0) palette.accentColor.copy(alpha = 0.12f)
                                else Color.Transparent
                            )
                            .clickable { onClipboardClick() }
                            .padding(horizontal = 6.dp, vertical = 5.dp)
                            .testTag("toolbar_clipboard"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "📋",
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (clipboardCount > 0) "Clipboard ($clipboardCount)" else "Clipboard",
                                color = if (clipboardCount > 0) palette.accentColor else palette.textColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onVaultClick() }
                            .padding(horizontal = 5.dp, vertical = 6.dp)
                            .testTag("toolbar_vault"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔐",
                            fontSize = 14.sp
                        )
                    }
                }

                // Suggestions List (Scrollable if many, fills available width)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Paste chip for the latest copied fragment
                    if (recentClip != null && onQuickPaste != null) {
                        val preview = if (recentClip.text.length > 18) recentClip.text.take(18) + "…" else recentClip.text
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.accentColor.copy(alpha = 0.16f))
                                .clickable { onQuickPaste(recentClip.text) }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                .testTag("toolbar_quick_paste_chip"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text("📋", fontSize = 10.sp)
                                Text(
                                    text = "Paste \"$preview\"",
                                    color = palette.accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (suggestions.isEmpty() && recentClip == null) {
                        Text(
                            text = "NXV Keyboard",
                            color = palette.secondaryTextColor.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    } else {
                        suggestions.forEachIndexed { index, item ->
                            val isPrimary = item.isPrimary

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp, vertical = 3.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isPrimary) palette.accentColor.copy(alpha = 0.14f)
                                        else Color.Transparent
                                    )
                                    .clickable { onSuggestionClick(item) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("suggestion_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.displayText,
                                    color = if (isPrimary) palette.accentColor else palette.textColor,
                                    fontSize = 14.sp,
                                    fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (index < suggestions.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .height(16.dp)
                                        .width(1.dp)
                                        .background(palette.dividerColor)
                                )
                            }
                        }
                    }
                }

                // Language toggle pill
                val langBadgeText = when (currentLanguage.lowercase()) {
                    "bangla" -> "বাংলা"
                    "avro" -> "অভ্র"
                    else -> "EN"
                }
                Box(
                    modifier = Modifier
                        .padding(end = 3.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.accentColor.copy(alpha = 0.15f))
                        .combinedClickable(
                            onClick = { onLanguageCycle() },
                            onLongClick = { onLanguageLongPress() }
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("toolbar_language_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "🌐",
                            fontSize = 11.sp
                        )
                        Text(
                            text = langBadgeText,
                            color = palette.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Quick Settings Gear icon
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onSettingsClick() }
                        .padding(horizontal = 5.dp, vertical = 6.dp)
                        .testTag("toolbar_settings"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚙️",
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Bottom border line for geometric structure
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(0.5.dp)
                .background(palette.dividerColor.copy(alpha = 0.35f))
        )
    }
}
