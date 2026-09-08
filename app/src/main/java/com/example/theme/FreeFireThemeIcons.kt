package com.example.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Custom Gaming Vector Icons and Components for Free Fire Black Gold Theme.
 */
object FreeFireThemeIcons {

    val GoldMetallic = Color(0xFFFFD700)
    val GoldDark = Color(0xFFC59B27)
    val GoldBright = Color(0xFFFFEA79)
    val DarkBase = Color(0xFF101318)

    /**
     * Angular Gaming Shift Arrow
     */
    @Composable
    fun FreeFireShiftIcon(
        size: Dp = 22.dp,
        isShifted: Boolean = false,
        isCapsLock: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        val arrowColor = if (isShifted || isCapsLock) GoldMetallic else Color.White
        val glowColor = if (isCapsLock) GoldBright else GoldMetallic

        Canvas(
            modifier = modifier
                .size(size)
                .drawBehind {
                    if (isShifted || isCapsLock) {
                        drawCircle(
                            color = glowColor.copy(alpha = if (isCapsLock) 0.5f else 0.3f),
                            radius = size.toPx() * 0.6f,
                            center = Offset(size.toPx() * 0.5f, size.toPx() * 0.5f)
                        )
                    }
                }
        ) {
            val w = this.size.width
            val h = this.size.height

            val path = Path().apply {
                // Sharp gaming arrow head
                moveTo(w * 0.5f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.52f)
                lineTo(w * 0.65f, h * 0.52f)
                lineTo(w * 0.65f, h * 0.85f)
                lineTo(w * 0.35f, h * 0.85f)
                lineTo(w * 0.35f, h * 0.52f)
                lineTo(w * 0.15f, h * 0.52f)
                close()
            }

            if (isShifted || isCapsLock) {
                drawPath(path = path, color = arrowColor)
                drawPath(path = path, color = GoldBright, style = Stroke(width = 1.5.dp.toPx()))
            } else {
                drawPath(path = path, color = Color.Transparent)
                drawPath(path = path, color = Color.White, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }

    /**
     * High-tech Crosshair Backspace Icon
     */
    @Composable
    fun FreeFireBackspaceIcon(
        size: Dp = 22.dp,
        isPressed: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        val strokeColor = if (isPressed) GoldMetallic else Color.White

        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Outer angular tag
            val tag = Path().apply {
                moveTo(w * 0.18f, h * 0.5f)
                lineTo(w * 0.42f, h * 0.20f)
                lineTo(w * 0.88f, h * 0.20f)
                lineTo(w * 0.88f, h * 0.80f)
                lineTo(w * 0.42f, h * 0.80f)
                close()
            }

            drawPath(
                path = tag,
                color = strokeColor,
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Inner cross 'X'
            drawLine(
                color = strokeColor,
                start = Offset(w * 0.54f, h * 0.38f),
                end = Offset(w * 0.74f, h * 0.62f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = strokeColor,
                start = Offset(w * 0.74f, h * 0.38f),
                end = Offset(w * 0.54f, h * 0.62f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }

    /**
     * Battle Royale Enter/Search Action Button
     */
    @Composable
    fun FreeFireEnterKeyContent(
        label: String,
        isPressed: Boolean,
        modifier: Modifier = Modifier
    ) {
        val isIconOnly = label == "↵" || label == "return"

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isIconOnly) {
                Canvas(modifier = Modifier.size(22.dp)) {
                    val w = size.width
                    val h = size.height

                    val enterPath = Path().apply {
                        moveTo(w * 0.80f, h * 0.32f)
                        lineTo(w * 0.80f, h * 0.68f)
                        lineTo(w * 0.25f, h * 0.68f)
                    }
                    drawPath(
                        path = enterPath,
                        color = if (isPressed) GoldBright else GoldMetallic,
                        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Arrow head pointing left
                    val arrowHead = Path().apply {
                        moveTo(w * 0.40f, h * 0.52f)
                        lineTo(w * 0.22f, h * 0.68f)
                        lineTo(w * 0.40f, h * 0.84f)
                    }
                    drawPath(
                        path = arrowHead,
                        color = if (isPressed) GoldBright else GoldMetallic,
                        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            } else {
                Text(
                    text = label,
                    color = if (isPressed) GoldBright else GoldMetallic,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
