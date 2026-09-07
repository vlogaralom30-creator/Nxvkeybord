package com.example.ui.keyboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theme.KeyboardPalette

/**
 * Enhanced, ergonomic Text Editing Keyboard Layout.
 * Features:
 * - Top Toolbar: Clear Select Mode switch pill (Illuminated status) + Quick action chips (Select All, Cut, Copy, Paste).
 * - Left Navigation Pane: Precision Directional DPad (Up, Down, Left, Right, Center Select Toggle)
 *   plus Jump controls (Line Start/End, Word Left/Right).
 * - Right Command Center: Card-based clipboard & editing actions (Cut, Copy, Paste, Select All, Undo, Redo, Delete, New Line).
 * - Bottom Return Bar: Dedicated ABC Keyboard return + Quick Space key.
 */
@Composable
fun TextEditingKeyboardLayout(
    keyHeight: Dp = 48.dp,
    palette: KeyboardPalette,
    onMoveUp: (isSelecting: Boolean) -> Unit,
    onMoveDown: (isSelecting: Boolean) -> Unit,
    onMoveLeft: (isSelecting: Boolean) -> Unit,
    onMoveRight: (isSelecting: Boolean) -> Unit,
    onMoveToStart: (isSelecting: Boolean) -> Unit,
    onMoveToEnd: (isSelecting: Boolean) -> Unit,
    onSelectAll: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    onCut: (() -> Unit)? = null,
    onUndo: (() -> Unit)? = null,
    onRedo: (() -> Unit)? = null,
    onMoveWordLeft: ((isSelecting: Boolean) -> Unit)? = null,
    onMoveWordRight: ((isSelecting: Boolean) -> Unit)? = null,
    onEnter: (() -> Unit)? = null,
    onSpace: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isSelecting by remember { mutableStateOf(false) }

    val selectPillBg by animateColorAsState(
        targetValue = if (isSelecting) palette.accentColor else palette.keyActionBackground,
        label = "select_pill_bg"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.keyboardBackground)
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .testTag("text_editing_keyboard_layout"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. TOP HEADER TOOLBAR: Select Mode Pill + Quick Action Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Select Mode Switch Pill
            Box(
                modifier = Modifier
                    .weight(1.7f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(selectPillBg)
                    .border(
                        width = if (isSelecting) 1.2.dp else 0.8.dp,
                        color = if (isSelecting) palette.accentColor else palette.keyBorderColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { isSelecting = !isSelecting }
                    .padding(horizontal = 8.dp)
                    .testTag("toggle_select_mode"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelecting) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Select Mode",
                        tint = if (isSelecting) palette.onAccentColor else palette.textColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (isSelecting) "Select: ON" else "Select Text",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelecting) palette.onAccentColor else palette.textColor
                    )
                }
            }

            // Quick Chips: Cut, Copy, Paste, Select All
            TopActionChip(
                label = "Cut",
                icon = Icons.Default.ContentCut,
                palette = palette,
                modifier = Modifier.weight(1f),
                onClick = { onCut?.invoke() ?: onCopy() }
            )
            TopActionChip(
                label = "Copy",
                icon = Icons.Default.ContentCopy,
                palette = palette,
                modifier = Modifier.weight(1f),
                onClick = onCopy
            )
            TopActionChip(
                label = "Paste",
                icon = Icons.Default.ContentPaste,
                palette = palette,
                modifier = Modifier.weight(1f),
                onClick = onPaste
            )
            TopActionChip(
                label = "All",
                icon = Icons.Default.SelectAll,
                palette = palette,
                modifier = Modifier.weight(0.9f),
                onClick = onSelectAll
            )
        }

        // 2. MAIN SECTION: 2 BALANCED SYMMETRICAL PANES
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyHeight * 3 + 24.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // LEFT PANE: Precision Directional DPad & Word Jumps
            Column(
                modifier = Modifier
                    .weight(1.35f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Line Start / End Jump Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    NavigationMiniKey(
                        label = "|< Start",
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onMoveToStart(isSelecting) }
                    )
                    NavigationMiniKey(
                        label = "End >|",
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onMoveToEnd(isSelecting) }
                    )
                }

                // Precision Directional DPAD (Cross / Diamond with Center Mode Toggle)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2.6f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.keyBackground)
                        .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // UP BUTTON
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.keyActionBackground)
                                .border(width = 0.6.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
                                .clickable { onMoveUp(isSelecting) }
                                .testTag("dpad_up"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Up",
                                tint = palette.textColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // MIDDLE ROW: LEFT, CENTER SELECT TOGGLE, RIGHT
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // LEFT BUTTON
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.keyActionBackground)
                                    .border(width = 0.6.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
                                    .clickable { onMoveLeft(isSelecting) }
                                    .testTag("dpad_left"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Left",
                                    tint = palette.textColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // CENTER TOGGLE (SEL / MOV)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelecting) palette.accentColor else palette.keyActionBackground)
                                    .border(
                                        width = 1.2.dp,
                                        color = if (isSelecting) palette.accentColor else palette.keyBorderColor,
                                        shape = CircleShape
                                    )
                                    .clickable { isSelecting = !isSelecting }
                                    .testTag("dpad_center_toggle"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isSelecting) "SEL" else "MOV",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelecting) palette.onAccentColor else palette.textColor
                                )
                            }

                            // RIGHT BUTTON
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.keyActionBackground)
                                    .border(width = 0.6.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
                                    .clickable { onMoveRight(isSelecting) }
                                    .testTag("dpad_right"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Right",
                                    tint = palette.textColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        // DOWN BUTTON
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.keyActionBackground)
                                .border(width = 0.6.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
                                .clickable { onMoveDown(isSelecting) }
                                .testTag("dpad_down"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Down",
                                tint = palette.textColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                // Word Jump Controls (Jump Left Word / Jump Right Word)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    NavigationWordKey(
                        label = "Word",
                        isLeft = true,
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (onMoveWordLeft != null) onMoveWordLeft(isSelecting)
                            else onMoveLeft(isSelecting)
                        }
                    )
                    NavigationWordKey(
                        label = "Word",
                        isLeft = false,
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (onMoveWordRight != null) onMoveWordRight(isSelecting)
                            else onMoveRight(isSelecting)
                        }
                    )
                }
            }

            // RIGHT PANE: Clipboard & Text Action Command Center (4 Rows x 2 Columns)
            Column(
                modifier = Modifier
                    .weight(1.4f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // ROW 1: Cut & Copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    EditCommandCard(
                        label = "Cut",
                        icon = Icons.Default.ContentCut,
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCut?.invoke() ?: onCopy() }
                    )
                    EditCommandCard(
                        label = "Copy",
                        icon = Icons.Default.ContentCopy,
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = onCopy
                    )
                }

                // ROW 2: Paste & Select All
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    EditCommandCard(
                        label = "Paste",
                        icon = Icons.Default.ContentPaste,
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = onPaste
                    )
                    EditCommandCard(
                        label = "Select All",
                        icon = Icons.Default.SelectAll,
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = onSelectAll
                    )
                }

                // ROW 3: Undo & Redo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    EditCommandCard(
                        label = "Undo",
                        icon = Icons.AutoMirrored.Filled.Undo,
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onUndo?.invoke() }
                    )
                    EditCommandCard(
                        label = "Redo",
                        icon = Icons.AutoMirrored.Filled.Redo,
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onRedo?.invoke() }
                    )
                }

                // ROW 4: Backspace (Delete) & New Line (Enter)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    EditCommandCard(
                        label = "Delete",
                        icon = Icons.AutoMirrored.Filled.Backspace,
                        palette = palette,
                        isDestructive = true,
                        modifier = Modifier.weight(1f),
                        onClick = onDelete
                    )
                    EditCommandCard(
                        label = "New Line",
                        icon = Icons.AutoMirrored.Filled.KeyboardReturn,
                        palette = palette,
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        onClick = { onEnter?.invoke() }
                    )
                }
            }
        }

        // 3. BOTTOM RETURN & QUICK SPACING BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ABC Keyboard Return Button
            Box(
                modifier = Modifier
                    .weight(2.2f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.keyActionBackground)
                    .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
                    .clickable { onClose() }
                    .testTag("textedit_close_abc"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Return to Keyboard",
                        tint = palette.textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "ABC Keyboard",
                        color = palette.textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Space Bar
            Box(
                modifier = Modifier
                    .weight(1.8f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.keyBackground)
                    .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
                    .clickable { onSpace?.invoke() }
                    .testTag("textedit_space"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SpaceBar,
                        contentDescription = "Space",
                        tint = palette.textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Space",
                        color = palette.textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun TopActionChip(
    label: String,
    icon: ImageVector,
    palette: KeyboardPalette,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(palette.keyActionBackground)
            .border(width = 0.7.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = palette.textColor, modifier = Modifier.size(13.dp))
            Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = palette.textColor)
        }
    }
}

@Composable
private fun NavigationMiniKey(
    label: String,
    palette: KeyboardPalette,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.keyActionBackground)
            .border(width = 0.7.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textColor
        )
    }
}

@Composable
private fun NavigationWordKey(
    label: String,
    isLeft: Boolean,
    palette: KeyboardPalette,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.keyActionBackground)
            .border(width = 0.7.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLeft) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                    contentDescription = "Word Left",
                    tint = palette.textColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textColor
            )
            if (!isLeft) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = "Word Right",
                    tint = palette.textColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun EditCommandCard(
    label: String,
    icon: ImageVector,
    palette: KeyboardPalette,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val bg = when {
        isPrimary -> palette.accentColor
        else -> palette.keyActionBackground
    }
    val contentColor = when {
        isPrimary -> palette.onAccentColor
        else -> palette.textColor
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(
                width = 0.8.dp,
                color = if (isPrimary) palette.accentColor else palette.keyBorderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .testTag("edit_card_${label.lowercase().replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
