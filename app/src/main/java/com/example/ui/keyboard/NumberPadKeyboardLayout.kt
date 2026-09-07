package com.example.ui.keyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theme.KeyboardPalette

enum class NumberPadMode {
    PHONE_DIAL,
    MATH_CALC
}

/**
 * Enhanced, ergonomic Number Pad & Phone Dialer layout.
 * Features:
 * - Dual Mode: "Phone Dialer (ফোন ডায়াল)" with USSD (*, #, +) & standard ABC letters,
 *   and "Calc & Math (হিসাব ও সংখ্যা)" with full arithmetic operators (+, -, ×, ÷, %, =).
 * - Instant One-Tap Toggle between English (123) and Bangla (১২৩) numerals with clear active styling.
 * - Standard Dial Pad with prominent primary digits and secondary letters (e.g. 2 ABC / ২).
 * - Quick Action Column with Backspace, Space, and Double-Height Accent Enter/Dial key.
 * - Bottom Quick Punctuation Bar (., :, -, ,) and dedicated Return to ABC Keyboard button.
 */
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
    var currentPadMode by remember { mutableStateOf(NumberPadMode.PHONE_DIAL) }

    val digitMap = remember {
        mapOf(
            "1" to "১", "2" to "২", "3" to "৩",
            "4" to "৪", "5" to "৫", "6" to "৬",
            "7" to "৭", "8" to "৮", "9" to "৯",
            "0" to "০"
        )
    }

    val phoneLettersMap = remember {
        mapOf(
            "1" to "",
            "2" to "ABC",
            "3" to "DEF",
            "4" to "GHI",
            "5" to "JKL",
            "6" to "MNO",
            "7" to "PQRS",
            "8" to "TUV",
            "9" to "WXYZ",
            "0" to "+"
        )
    }

    fun getDisplayDigit(digit: String): String {
        return if (isBanglaDigits) digitMap[digit] ?: digit else digit
    }

    fun getSubText(digit: String): String {
        return if (isBanglaDigits) {
            // Show English digit as subtle reference
            digit
        } else {
            phoneLettersMap[digit] ?: ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.keyboardBackground)
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .testTag("number_pad_keyboard_layout"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. TOP HEADER TOOLBAR: Mode Switcher Tabs & Bengali/English Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode Tab 1: Phone Dialer
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (currentPadMode == NumberPadMode.PHONE_DIAL) palette.accentColor.copy(alpha = 0.22f)
                        else palette.keyActionBackground
                    )
                    .border(
                        width = if (currentPadMode == NumberPadMode.PHONE_DIAL) 1.2.dp else 0.8.dp,
                        color = if (currentPadMode == NumberPadMode.PHONE_DIAL) palette.accentColor else palette.keyBorderColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { currentPadMode = NumberPadMode.PHONE_DIAL }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone Dial",
                        tint = if (currentPadMode == NumberPadMode.PHONE_DIAL) palette.accentColor else palette.textColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Phone Dial",
                        fontSize = 12.sp,
                        fontWeight = if (currentPadMode == NumberPadMode.PHONE_DIAL) FontWeight.Bold else FontWeight.Medium,
                        color = if (currentPadMode == NumberPadMode.PHONE_DIAL) palette.accentColor else palette.textColor
                    )
                }
            }

            // Mode Tab 2: Calc / Math
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (currentPadMode == NumberPadMode.MATH_CALC) palette.accentColor.copy(alpha = 0.22f)
                        else palette.keyActionBackground
                    )
                    .border(
                        width = if (currentPadMode == NumberPadMode.MATH_CALC) 1.2.dp else 0.8.dp,
                        color = if (currentPadMode == NumberPadMode.MATH_CALC) palette.accentColor else palette.keyBorderColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { currentPadMode = NumberPadMode.MATH_CALC }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculator",
                        tint = if (currentPadMode == NumberPadMode.MATH_CALC) palette.accentColor else palette.textColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Calc / Math",
                        fontSize = 12.sp,
                        fontWeight = if (currentPadMode == NumberPadMode.MATH_CALC) FontWeight.Bold else FontWeight.Medium,
                        color = if (currentPadMode == NumberPadMode.MATH_CALC) palette.accentColor else palette.textColor
                    )
                }
            }

            // Language Toggle Chip (English 123 vs Bangla ১২৩)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isBanglaDigits) palette.accentColor else palette.keyActionBackground)
                    .border(
                        width = 1.dp,
                        color = if (isBanglaDigits) palette.accentColor else palette.keyBorderColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { isBanglaDigits = !isBanglaDigits }
                    .padding(horizontal = 6.dp)
                    .testTag("toggle_bangla_digits"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBanglaDigits) "বাংলা (১২৩)" else "Eng (123)",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBanglaDigits) palette.onAccentColor else palette.textColor
                )
            }
        }

        // 2. QUICK SYMBOLS / OPERATORS BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val quickSymbols = if (currentPadMode == NumberPadMode.PHONE_DIAL) {
                listOf("+", "*", "#", "-", "(", ")")
            } else {
                listOf("+", "-", "×", "÷", "%", "=")
            }

            quickSymbols.forEach { symbol ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.keyActionBackground)
                        .border(width = 0.7.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(8.dp))
                        .clickable { onCharTyped(symbol) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textColor
                    )
                }
            }
        }

        // 3. MAIN DIAL PAD & ACTION GRID (3 Columns of Digits + 1 Column of Actions)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyHeight * 4 + 18.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // LEFT 3 COLUMNS: DIAL DIGITS
            Column(
                modifier = Modifier
                    .weight(3.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // ROW 1: 1, 2, 3
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DialDigitKey(
                        digit = getDisplayDigit("1"),
                        subText = getSubText("1"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("1")) }
                    )
                    DialDigitKey(
                        digit = getDisplayDigit("2"),
                        subText = getSubText("2"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("2")) }
                    )
                    DialDigitKey(
                        digit = getDisplayDigit("3"),
                        subText = getSubText("3"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("3")) }
                    )
                }

                // ROW 2: 4, 5, 6
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DialDigitKey(
                        digit = getDisplayDigit("4"),
                        subText = getSubText("4"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("4")) }
                    )
                    DialDigitKey(
                        digit = getDisplayDigit("5"),
                        subText = getSubText("5"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("5")) }
                    )
                    DialDigitKey(
                        digit = getDisplayDigit("6"),
                        subText = getSubText("6"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("6")) }
                    )
                }

                // ROW 3: 7, 8, 9
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DialDigitKey(
                        digit = getDisplayDigit("7"),
                        subText = getSubText("7"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("7")) }
                    )
                    DialDigitKey(
                        digit = getDisplayDigit("8"),
                        subText = getSubText("8"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("8")) }
                    )
                    DialDigitKey(
                        digit = getDisplayDigit("9"),
                        subText = getSubText("9"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("9")) }
                    )
                }

                // ROW 4: Star/Dot, 0(+), Hash/Comma
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val leftSymbol = if (currentPadMode == NumberPadMode.PHONE_DIAL) "*" else "."
                    val rightSymbol = if (currentPadMode == NumberPadMode.PHONE_DIAL) "#" else ","

                    // Left Special Key (* or .)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.keyActionBackground)
                            .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(12.dp))
                            .clickable { onCharTyped(leftSymbol) }
                            .testTag("numpad_key_star_dot"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = leftSymbol,
                            color = palette.textColor,
                            fontSize = if (leftSymbol == "*") 26.sp else 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Center 0 Key with "+" sub-action
                    DialDigitKey(
                        digit = getDisplayDigit("0"),
                        subText = if (currentPadMode == NumberPadMode.PHONE_DIAL) "+" else getSubText("0"),
                        palette = palette,
                        modifier = Modifier.weight(1f),
                        onClick = { onCharTyped(getDisplayDigit("0")) }
                    )

                    // Right Special Key (# or ,)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.keyActionBackground)
                            .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(12.dp))
                            .clickable { onCharTyped(rightSymbol) }
                            .testTag("numpad_key_hash_comma"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rightSymbol,
                            color = palette.textColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // RIGHT 1 COLUMN: BACKSPACE, SPACE & TALL ACCENT ENTER KEY
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Backspace Key
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.keyActionBackground)
                        .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(12.dp))
                        .clickable { onDelete() }
                        .testTag("numpad_backspace"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = palette.textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Space Bar Key
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.keyBackground)
                        .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(12.dp))
                        .clickable { onSpace() }
                        .testTag("numpad_space"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SpaceBar,
                        contentDescription = "Space",
                        tint = palette.textColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Tall Accent Enter / Search Key (Spans 2 Weights)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.accentColor)
                        .clickable { onEnter() }
                        .testTag("numpad_enter"),
                    contentAlignment = Alignment.Center
                ) {
                    if (enterLabel.lowercase().contains("search")) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = palette.onAccentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                            contentDescription = "Enter",
                            tint = palette.onAccentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // 4. BOTTOM RETURN & QUICK PUNCTUATION BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick Return to ABC Keyboard Button
            Box(
                modifier = Modifier
                    .weight(2.4f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.keyActionBackground)
                    .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
                    .clickable { onClose() }
                    .testTag("numpad_close_abc"),
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

            // Quick Punctuation Keys (., ,, :, /)
            listOf(".", ",", ":", "/").forEach { punc ->
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.keyBackground)
                        .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(10.dp))
                        .clickable { onCharTyped(punc) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = punc,
                        color = palette.textColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Polished Dial Digit Key with primary digit and sub-label letters.
 */
@Composable
private fun DialDigitKey(
    digit: String,
    subText: String,
    palette: KeyboardPalette,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.keyBackground)
            .border(width = 0.8.dp, color = palette.keyBorderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("dial_digit_$digit"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = digit,
                color = palette.textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            if (subText.isNotEmpty()) {
                Text(
                    text = subText,
                    color = palette.secondaryTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
