package com.example.ui.keyboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theme.KeyboardPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun KeyboardKeyView(
    label: String,
    modifier: Modifier = Modifier,
    subLabel: String? = null,
    isSpecialAction: Boolean = false,
    isPrimaryAction: Boolean = false,
    isSpaceBar: Boolean = false,
    isRepeatable: Boolean = false,
    isShiftActive: Boolean = false,
    isCapsLock: Boolean = false,
    height: Dp = 46.dp,
    palette: KeyboardPalette,
    showPreview: Boolean = false,
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(durationMillis = 70),
        label = "key_scale"
    )

    val bgColor = when {
        isPressed -> when {
            isCapsLock -> palette.accentColor.copy(alpha = 0.85f)
            isPrimaryAction -> Color(0xFFB5D1F8)
            isSpecialAction || isShiftActive -> palette.keyPressedBackground
            else -> palette.keyActionBackground
        }
        isCapsLock -> palette.accentColor
        isShiftActive -> palette.keyPressedBackground
        isPrimaryAction -> palette.accentColor
        isSpecialAction -> palette.keyActionBackground
        else -> palette.keyBackground
    }

    val labelColor = when {
        isCapsLock || isPrimaryAction -> palette.onAccentColor
        isShiftActive -> palette.accentColor
        isSpaceBar -> palette.secondaryTextColor
        else -> palette.textColor
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.5.dp)
            .height(height)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.5.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color(0x33000000)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(
                width = 0.5.dp,
                color = if (isPrimaryAction || isCapsLock) Color.Transparent else palette.dividerColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(isRepeatable) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (isRepeatable) {
                            // Immediate execution for responsive first tap
                            onTap()

                            // Coroutine job for continuous repeating while key remains held
                            val repeatJob = coroutineScope.launch {
                                delay(380L) // Initial threshold before continuous repetition starts
                                var currentDelay = 65L
                                var holdDuration = 0L
                                while (isActive) {
                                    onTap()
                                    delay(currentDelay)
                                    holdDuration += currentDelay
                                    // Smoothly accelerate deletion speed when holding longer
                                    if (holdDuration > 1400L) {
                                        currentDelay = 35L
                                    } else if (holdDuration > 700L) {
                                        currentDelay = 50L
                                    }
                                }
                            }
                            try {
                                tryAwaitRelease()
                            } finally {
                                // Stop immediately on release or gesture cancellation - no background deletion
                                repeatJob.cancel()
                                isPressed = false
                            }
                        } else {
                            try {
                                tryAwaitRelease()
                            } finally {
                                isPressed = false
                            }
                        }
                    },
                    onTap = {
                        // For non-repeatable keys, handle standard tap
                        if (!isRepeatable) {
                            onTap()
                        }
                    },
                    onLongPress = {
                        if (!isRepeatable) {
                            onLongPress?.invoke() ?: onTap()
                        }
                    }
                )
            }
            .testTag("key_$label"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = labelColor,
                fontSize = if (label.length > 2) 13.sp else 18.sp,
                fontWeight = when {
                    isPrimaryAction || isCapsLock -> FontWeight.Bold
                    isSpecialAction || isShiftActive -> FontWeight.SemiBold
                    else -> FontWeight.Medium
                },
                maxLines = 1
            )

            // CapsLock active indicator bar
            if (isCapsLock) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 3.5.dp)
                        .width(14.dp)
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(palette.onAccentColor)
                )
            } else if (isShiftActive) {
                // One-time Shift active indicator dot
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                        .width(5.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(palette.accentColor)
                )
            }

            // Spacebar geometric balance indicator bar
            if (isSpaceBar) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp)
                        .width(36.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(palette.dividerColor)
                )
            }

            // Optional subLabel (like alt symbol) in top-right corner
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    color = palette.secondaryTextColor.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 4.dp)
                )
            }
        }
    }
}
