package com.example.ui.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.keyboard.KeyboardMode
import com.example.language.bangla.BanglaLayouts
import com.example.theme.KeyboardPalette

@Composable
fun BanglaKeyLayout(
    isShift: Boolean,
    keyHeight: Dp,
    palette: KeyboardPalette,
    enterLabel: String,
    onCharTyped: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onSpaceDrag: ((Float) -> Unit)? = null,
    onEnter: () -> Unit,
    onShift: () -> Unit,
    onSwitchMode: (KeyboardMode) -> Unit,
    onLanguageCycle: () -> Unit,
    onLongPressLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = if (isShift) BanglaLayouts.SHIFT_ROWS else BanglaLayouts.NORMAL_ROWS

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            rows[0].forEach { char ->
                KeyboardKeyView(
                    label = char,
                    modifier = Modifier.weight(1f),
                    height = keyHeight,
                    palette = palette,
                    onTap = { onCharTyped(char) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            rows[1].forEach { char ->
                KeyboardKeyView(
                    label = char,
                    modifier = Modifier.weight(1f),
                    height = keyHeight,
                    palette = palette,
                    onTap = { onCharTyped(char) }
                )
            }
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            rows[2].forEach { char ->
                KeyboardKeyView(
                    label = char,
                    modifier = Modifier.weight(1f),
                    height = keyHeight,
                    palette = palette,
                    onTap = { onCharTyped(char) }
                )
            }
        }

        // Row 4: [Shift] char1 char2 ... [Backspace]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift
            KeyboardKeyView(
                label = if (isShift) "স্বর" else "ব্যঞ্জন",
                modifier = Modifier.weight(1.4f),
                isSpecialAction = true,
                height = keyHeight,
                palette = palette,
                onTap = { onShift() }
            )

            rows[3].forEach { char ->
                KeyboardKeyView(
                    label = char,
                    modifier = Modifier.weight(1f),
                    height = keyHeight,
                    palette = palette,
                    onTap = { onCharTyped(char) }
                )
            }

            // Backspace
            KeyboardKeyView(
                label = "⌫",
                modifier = Modifier.weight(1.4f),
                isSpecialAction = true,
                isRepeatable = true,
                height = keyHeight,
                palette = palette,
                onTap = { onDelete() }
            )
        }

        // Bottom Row: [123] [😊] [🌐] [    Space (বাংলা)    ] [।] [↵]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyboardKeyView(
                label = "১২৩",
                modifier = Modifier.weight(1.3f),
                isSpecialAction = true,
                height = keyHeight,
                palette = palette,
                onTap = { onSwitchMode(KeyboardMode.NUMBERS) }
            )

            KeyboardKeyView(
                label = "😊",
                modifier = Modifier.weight(1.0f),
                isSpecialAction = true,
                height = keyHeight,
                palette = palette,
                onTap = { onSwitchMode(KeyboardMode.EMOJI) }
            )

            KeyboardKeyView(
                label = "🌐",
                modifier = Modifier.weight(1.0f),
                isSpecialAction = true,
                height = keyHeight,
                palette = palette,
                onTap = { onLanguageCycle() },
                onLongPress = { onLongPressLanguage() }
            )

            KeyboardKeyView(
                label = "বাংলা",
                modifier = Modifier.weight(3.5f),
                isSpaceBar = true,
                height = keyHeight,
                palette = palette,
                onHorizontalDrag = onSpaceDrag,
                onTap = { onSpace() }
            )

            KeyboardKeyView(
                label = "।",
                modifier = Modifier.weight(1.0f),
                isSpecialAction = false,
                height = keyHeight,
                palette = palette,
                onTap = { onCharTyped("।") }
            )

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
