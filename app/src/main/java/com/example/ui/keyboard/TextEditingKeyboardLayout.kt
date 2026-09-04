package com.example.ui.keyboard

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theme.KeyboardPalette

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
    modifier: Modifier = Modifier
) {
    var isSelecting by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Main Navigation Pad Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyHeight * 3 + 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1. LEFT Arrow (Spans height)
            TextEditActionCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                palette = palette,
                onClick = { onMoveLeft(isSelecting) }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Move Left",
                    tint = palette.textColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // 2. Center Column: Up, Select Toggle, Down
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // UP Arrow
                TextEditActionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    palette = palette,
                    onClick = { onMoveUp(isSelecting) }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move Up",
                        tint = palette.textColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // SELECT Toggle Button
                TextEditActionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    palette = palette,
                    isActive = isSelecting,
                    activeColor = palette.accentColor,
                    onClick = { isSelecting = !isSelecting }
                ) {
                    Text(
                        text = if (isSelecting) "Select ON" else "Select",
                        color = if (isSelecting) Color.White else palette.textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // DOWN Arrow
                TextEditActionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    palette = palette,
                    onClick = { onMoveDown(isSelecting) }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move Down",
                        tint = palette.textColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // 3. RIGHT Arrow (Spans height)
            TextEditActionCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                palette = palette,
                onClick = { onMoveRight(isSelecting) }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Move Right",
                    tint = palette.textColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // 4. Right Column: Select All, Copy, Paste
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TextEditActionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    palette = palette,
                    onClick = onSelectAll
                ) {
                    Text(
                        text = "Select All",
                        color = palette.textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextEditActionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    palette = palette,
                    onClick = onCopy
                ) {
                    Text(
                        text = "Copy",
                        color = palette.textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextEditActionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    palette = palette,
                    onClick = onPaste
                ) {
                    Text(
                        text = "Paste",
                        color = palette.textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Bottom Row: Home (|<), End (>|), Backspace, Close
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Home / Start of line
            TextEditActionCard(
                modifier = Modifier.weight(1f),
                palette = palette,
                onClick = { onMoveToStart(isSelecting) }
            ) {
                Text(
                    text = "|<",
                    color = palette.textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // End / End of line
            TextEditActionCard(
                modifier = Modifier.weight(1f),
                palette = palette,
                onClick = { onMoveToEnd(isSelecting) }
            ) {
                Text(
                    text = ">|",
                    color = palette.textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Backspace
            TextEditActionCard(
                modifier = Modifier.weight(1f),
                palette = palette,
                isAction = true,
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Backspace",
                    tint = palette.textColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Close / Return to QWERTY
            TextEditActionCard(
                modifier = Modifier.weight(1f),
                palette = palette,
                isAction = true,
                onClick = onClose
            ) {
                Text(
                    text = "ABC",
                    color = palette.textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TextEditActionCard(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    isActive: Boolean = false,
    isAction: Boolean = false,
    activeColor: Color = Color.Unspecified,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val bg = when {
        isActive -> if (activeColor != Color.Unspecified) activeColor else palette.accentColor
        isAction -> palette.keyActionBackground
        else -> palette.keyBackground
    }

    val borderCol = if (isActive) palette.accentColor else palette.keyBorderColor.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(width = 0.8.dp, color = borderCol, shape = RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
