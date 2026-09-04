package com.example.ui.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.keyboard.KeyboardMode
import com.example.keyboard.ShiftState
import com.example.language.english.EnglishEngine
import com.example.theme.KeyboardPalette

@Composable
fun QwertyKeyLayout(
    isAvro: Boolean,
    shiftState: ShiftState,
    showNumberRow: Boolean,
    keyHeight: Dp,
    palette: KeyboardPalette,
    enterLabel: String,
    onCharTyped: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onShift: () -> Unit,
    onSwitchMode: (KeyboardMode) -> Unit,
    onLanguageCycle: () -> Unit,
    onLongPressLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isShifted = shiftState.isUppercase

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        // Optional Number Row
        if (showNumberRow) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                EnglishEngine.NUMBER_ROW.forEach { num ->
                    KeyboardKeyView(
                        label = num,
                        modifier = Modifier.weight(1f),
                        height = keyHeight * 0.85f,
                        palette = palette,
                        onTap = { onCharTyped(num) }
                    )
                }
            }
        }

        // Row 1: Q W E R T Y U I O P
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            EnglishEngine.QWERTY_ROW_1.forEachIndexed { index, letter ->
                val char = if (isShifted) letter.uppercase() else letter.lowercase()
                val alt = EnglishEngine.NUMBER_ROW.getOrNull(index)
                KeyboardKeyView(
                    label = char,
                    subLabel = if (!showNumberRow) alt else null,
                    modifier = Modifier.weight(1f),
                    height = keyHeight,
                    palette = palette,
                    onTap = { onCharTyped(char) },
                    onLongPress = { alt?.let { onCharTyped(it) } }
                )
            }
        }

        // Row 2: A S D F G H J K L (indented)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            EnglishEngine.QWERTY_ROW_2.forEach { letter ->
                val char = if (isShifted) letter.uppercase() else letter.lowercase()
                KeyboardKeyView(
                    label = char,
                    modifier = Modifier.weight(1f),
                    height = keyHeight,
                    palette = palette,
                    onTap = { onCharTyped(char) }
                )
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

        // Row 3: [Shift] Z X C V B N M [Backspace]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift Key with clear visual states
            val shiftIcon = when (shiftState) {
                ShiftState.LOWERCASE -> "⇧"
                ShiftState.SHIFT_ONCE -> "⬆"
                ShiftState.CAPS_LOCK -> "⇪"
            }
            KeyboardKeyView(
                label = shiftIcon,
                modifier = Modifier.weight(1.5f),
                isSpecialAction = true,
                isCapsLock = shiftState == ShiftState.CAPS_LOCK,
                isShiftActive = shiftState == ShiftState.SHIFT_ONCE,
                height = keyHeight,
                palette = palette,
                onTap = { onShift() }
            )

            EnglishEngine.QWERTY_ROW_3.forEach { letter ->
                val char = if (isShifted) letter.uppercase() else letter.lowercase()
                KeyboardKeyView(
                    label = char,
                    modifier = Modifier.weight(1f),
                    height = keyHeight,
                    palette = palette,
                    onTap = { onCharTyped(char) }
                )
            }

            // Backspace Key with continuous repeating on hold
            KeyboardKeyView(
                label = "⌫",
                modifier = Modifier.weight(1.5f),
                isSpecialAction = true,
                isRepeatable = true,
                height = keyHeight,
                palette = palette,
                onTap = { onDelete() }
            )
        }

        // Row 4: [?123] [😊] [🌐] [    Space    ] [.] [↵]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numbers & Symbols switch (?123)
            KeyboardKeyView(
                label = "?123",
                modifier = Modifier.weight(1.3f),
                isSpecialAction = true,
                height = keyHeight,
                palette = palette,
                onTap = { onSwitchMode(KeyboardMode.NUMBERS) }
            )

            // Emoji
            KeyboardKeyView(
                label = "😊",
                modifier = Modifier.weight(1.0f),
                isSpecialAction = true,
                height = keyHeight,
                palette = palette,
                onTap = { onSwitchMode(KeyboardMode.EMOJI) }
            )

            // Language Switch 🌐
            KeyboardKeyView(
                label = "🌐",
                modifier = Modifier.weight(1.0f),
                isSpecialAction = true,
                height = keyHeight,
                palette = palette,
                onTap = { onLanguageCycle() },
                onLongPress = { onLongPressLanguage() }
            )

            // Space Bar with language badge and geometric indicator
            val spaceLabel = if (isAvro) "AVRO" else "SPACE"
            KeyboardKeyView(
                label = spaceLabel,
                modifier = Modifier.weight(4.0f),
                isSpaceBar = true,
                height = keyHeight,
                palette = palette,
                onTap = { onSpace() }
            )

            // Punctuation (comma/period/dari)
            val punctChar = if (isAvro) "।" else "."
            KeyboardKeyView(
                label = punctChar,
                modifier = Modifier.weight(1.0f),
                isSpecialAction = false,
                height = keyHeight,
                palette = palette,
                onTap = { onCharTyped(punctChar) }
            )

            // Enter Key (Geometric Balance focal accent)
            KeyboardKeyView(
                label = enterLabel,
                modifier = Modifier.weight(1.4f),
                isSpecialAction = true,
                isPrimaryAction = true,
                height = keyHeight,
                palette = palette,
                onTap = { onEnter() }
            )
        }
    }
}
