package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theme.KeyboardPalette

@Composable
fun NumberPadKeyboardLayout(
    keyHeight: Dp = 48.dp,
    palette: KeyboardPalette,
    enterLabel: String = "↵",
    onCharTyped: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBanglaDigits by remember { mutableStateOf(false) }

    val digitMap = mapOf(
        "1" to "১", "2" to "২", "3" to "৩",
        "4" to "৪", "5" to "৫", "6" to "৬",
        "7" to "৭", "8" to "৮", "9" to "৯",
        "0" to "০"
    )

    fun getDigit(d: String): String {
        return if (isBanglaDigits) digitMap[d] ?: d else d
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Main Numpad Body
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyHeight * 4 + 18.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1. Left Operator Column: +, -, *, /
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NumpadKeyCard(
                    modifier = Modifier.weight(1f),
                    palette = palette,
                    isAction = true,
                    onClick = { onCharTyped("+") }
                ) {
                    Text("+", color = palette.textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                NumpadKeyCard(
                    modifier = Modifier.weight(1f),
                    palette = palette,
                    isAction = true,
                    onClick = { onCharTyped("-") }
                ) {
                    Text("-", color = palette.textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                NumpadKeyCard(
                    modifier = Modifier.weight(1f),
                    palette = palette,
                    isAction = true,
                    onClick = { onCharTyped("*") }
                ) {
                    Text("*", color = palette.textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                NumpadKeyCard(
                    modifier = Modifier.weight(1f),
                    palette = palette,
                    isAction = true,
                    onClick = { onCharTyped("/") }
                ) {
                    Text("/", color = palette.textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 2. Right Grid (4x4)
            Column(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Row 1: 1, 2, 3, %
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NumpadDigitKey(getDigit("1"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("1")) }
                    NumpadDigitKey(getDigit("2"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("2")) }
                    NumpadDigitKey(getDigit("3"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("3")) }
                    NumpadKeyCard(Modifier.weight(1f), palette, isAction = true, onClick = { onCharTyped("%") }) {
                        Text("%", color = palette.textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Row 2: 4, 5, 6, Bangla digits switch (১২৩)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NumpadDigitKey(getDigit("4"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("4")) }
                    NumpadDigitKey(getDigit("5"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("5")) }
                    NumpadDigitKey(getDigit("6"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("6")) }
                    NumpadKeyCard(
                        Modifier.weight(1f),
                        palette,
                        isActive = isBanglaDigits,
                        activeColor = palette.accentColor,
                        isAction = true,
                        onClick = { isBanglaDigits = !isBanglaDigits }
                    ) {
                        Text(
                            text = "১২৩",
                            color = if (isBanglaDigits) Color.White else palette.textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Row 3: 7, 8, 9, Backspace
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NumpadDigitKey(getDigit("7"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("7")) }
                    NumpadDigitKey(getDigit("8"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("8")) }
                    NumpadDigitKey(getDigit("9"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("9")) }
                    NumpadKeyCard(Modifier.weight(1f), palette, isAction = true, onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            tint = palette.textColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Row 4: ., 0, Space, Enter / Search
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NumpadKeyCard(Modifier.weight(1f), palette, onClick = { onCharTyped(".") }) {
                        Text(".", color = palette.textColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    NumpadDigitKey(getDigit("0"), palette, Modifier.weight(1f)) { onCharTyped(getDigit("0")) }
                    NumpadKeyCard(Modifier.weight(1f), palette, onClick = onSpace) {
                        Text("Space", color = palette.secondaryTextColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    NumpadKeyCard(
                        Modifier.weight(1f),
                        palette,
                        isActive = true,
                        activeColor = palette.accentColor,
                        onClick = onEnter
                    ) {
                        if (enterLabel.lowercase().contains("search")) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = enterLabel,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Bottom Bar: Return to ABC
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyHeight),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NumpadKeyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(keyHeight),
                palette = palette,
                isAction = true,
                onClick = onClose
            ) {
                Text(
                    text = "ABC",
                    color = palette.textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NumpadDigitKey(
    text: String,
    palette: KeyboardPalette,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    NumpadKeyCard(
        modifier = modifier,
        palette = palette,
        onClick = onClick
    ) {
        Text(
            text = text,
            color = palette.textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun NumpadKeyCard(
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
