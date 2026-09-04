package com.example.ui.keyboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.theme.KeyPopupStyle
import com.example.theme.KeyboardPalette
import com.example.theme.PuppyPopupCharacter
import com.example.theme.SpacebarStyle
import com.example.theme.StrawberryThemeIcons
import com.example.theme.ThemeSpecialIconStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun KeyboardKeyView(
    label: String,
    modifier: Modifier = Modifier,
    subLabel: String? = null,
    showSubLabel: Boolean = true,
    popupMode: String = "popup",
    isSpecialAction: Boolean = false,
    isPrimaryAction: Boolean = false,
    isSpaceBar: Boolean = false,
    isRepeatable: Boolean = false,
    isShiftActive: Boolean = false,
    isCapsLock: Boolean = false,
    height: Dp = 46.dp,
    palette: KeyboardPalette,
    showPreview: Boolean = true,
    onHorizontalDrag: ((Float) -> Unit)? = null,
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(durationMillis = 60),
        label = "key_scale"
    )

    // Determine Key Background Color based on Theme & State
    val bgColor = when {
        isPressed -> when {
            palette.popupStyle == KeyPopupStyle.PUPPY_CHARACTER && label.length == 1 && label[0].isLetter() -> palette.keyPressedBackground // Vibrant blue for Puppy Pop!
            isCapsLock -> palette.accentColor.copy(alpha = 0.85f)
            isPrimaryAction -> palette.accentColor
            isSpecialAction || isShiftActive -> palette.keyPressedBackground
            else -> palette.keyPressedBackground
        }
        isCapsLock -> palette.accentColor
        isShiftActive -> palette.keyPressedBackground
        isPrimaryAction -> if (palette.specialIconStyle == ThemeSpecialIconStyle.PUPPY_MINIMAL) palette.keyActionBackground else palette.accentColor
        isSpecialAction -> palette.keyActionBackground
        else -> palette.keyBackground
    }

    // Determine Label Text Color based on Theme & State
    val labelColor = when {
        isPressed && palette.popupStyle == KeyPopupStyle.PUPPY_CHARACTER && label.length == 1 && label[0].isLetter() -> Color.White
        isCapsLock || (isPrimaryAction && palette.specialIconStyle != ThemeSpecialIconStyle.PUPPY_MINIMAL) -> palette.onAccentColor
        isShiftActive && palette.specialIconStyle != ThemeSpecialIconStyle.STRAWBERRY_DESSERT -> palette.accentColor
        isSpaceBar -> palette.secondaryTextColor
        isSpecialAction -> palette.textColor
        else -> palette.textColor
    }

    // Key shape & elevation from theme
    val cornerShape = RoundedCornerShape(palette.keyCornerRadius)
    val elevation = if (isPressed) palette.pressedElevation else palette.keyElevation
    val borderColor = if (isPrimaryAction && palette.specialIconStyle != ThemeSpecialIconStyle.STRAWBERRY_DESSERT) {
        Color.Transparent
    } else {
        palette.keyBorderColor
    }

    val gestureModifier = if (isSpaceBar && onHorizontalDrag != null) {
        Modifier.pointerInput(onHorizontalDrag) {
            awaitEachGesture {
                val down = awaitFirstDown()
                isPressed = true
                var isDragging = false
                val touchSlop = viewConfiguration.touchSlop
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (change.isConsumed) break
                    if (!change.pressed) {
                        if (!isDragging) {
                            onTap()
                        }
                        break
                    }
                    val totalDist = kotlin.math.abs(change.position.x - down.position.x)
                    if (!isDragging && totalDist > touchSlop) {
                        isDragging = true
                    }
                    if (isDragging) {
                        val dx = change.positionChange().x
                        if (dx != 0f) {
                            change.consume()
                            onHorizontalDrag(dx)
                        }
                    }
                }
                isPressed = false
            }
        }
    } else {
        Modifier.pointerInput(isRepeatable) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    if (isRepeatable) {
                        onTap()
                        val repeatJob = coroutineScope.launch {
                            delay(380L)
                            var currentDelay = 65L
                            var holdDuration = 0L
                            while (isActive) {
                                onTap()
                                delay(currentDelay)
                                holdDuration += currentDelay
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
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.5.dp)
            .height(height)
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = cornerShape,
                spotColor = Color(0x35000000)
            )
            .clip(cornerShape)
            .background(bgColor)
            .border(
                width = palette.keyBorderWidth,
                color = borderColor,
                shape = cornerShape
            )
            .then(gestureModifier)
            .testTag("key_$label"),
        contentAlignment = Alignment.Center
    ) {
        // Main key surface content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Check for theme-specific special icon drawings
            when {
                // 1. STRAWBERRY DESSERT THEME SPECIAL ICONS
                palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT && (label == "⇧" || label == "⬆" || label == "⇪") -> {
                    StrawberryThemeIcons.StrawberryShiftIcon(
                        size = (height * 0.58f).coerceIn(20.dp, 28.dp),
                        isShifted = isShiftActive || isCapsLock
                    )
                }
                palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT && label == "⌫" -> {
                    StrawberryThemeIcons.FrappeBackspaceIcon(
                        size = (height * 0.58f).coerceIn(20.dp, 28.dp)
                    )
                }
                palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT && (label == "😊" || label == "🌐" || label == "?123" || label == "#+=") -> {
                    if (label == "😊") {
                        StrawberryThemeIcons.IceCreamIcon(size = (height * 0.58f).coerceIn(20.dp, 28.dp))
                    } else {
                        Text(
                            text = if (label == "🌐") "lolo" else label,
                            color = palette.textColor,
                            fontSize = if (label == "🌐") 11.sp else 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT && (isPrimaryAction || label == "↵" || label == "Go" || label == "Search" || label == "Done") -> {
                    StrawberryThemeIcons.ShortcakeEnterIcon(
                        size = (height * 0.62f).coerceIn(22.dp, 30.dp)
                    )
                }

                // 2. PUPPY POP MINIMAL ICONS (Reference Image 1)
                palette.specialIconStyle == ThemeSpecialIconStyle.PUPPY_MINIMAL && (label == "⇧" || label == "⬆" || label == "⇪") -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // Minimalist thin arrow with dot (as seen in Image 1)
                        Text(
                            text = "↑",
                            color = palette.textColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal
                        )
                        // Top-left dot indicator
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 4.dp, start = 6.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isShiftActive || isCapsLock) palette.accentColor
                                    else palette.secondaryTextColor.copy(alpha = 0.5f)
                                )
                        )
                    }
                }
                palette.specialIconStyle == ThemeSpecialIconStyle.PUPPY_MINIMAL && label == "⌫" -> {
                    // Minimalist backspace symbol
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val w = size.width
                        val h = size.height
                        val tagPath = Path().apply {
                            moveTo(w * 0.25f, h * 0.5f)
                            lineTo(w * 0.45f, h * 0.22f)
                            lineTo(w * 0.85f, h * 0.22f)
                            lineTo(w * 0.85f, h * 0.78f)
                            lineTo(w * 0.45f, h * 0.78f)
                            close()
                        }
                        drawPath(path = tagPath, color = palette.textColor, style = Stroke(width = 1.8f))
                        // 'X' inside
                        drawLine(
                            color = palette.textColor,
                            start = Offset(w * 0.54f, h * 0.38f),
                            end = Offset(w * 0.74f, h * 0.62f),
                            strokeWidth = 1.6f
                        )
                        drawLine(
                            color = palette.textColor,
                            start = Offset(w * 0.74f, h * 0.38f),
                            end = Offset(w * 0.54f, h * 0.62f),
                            strokeWidth = 1.6f
                        )
                    }
                }
                palette.specialIconStyle == ThemeSpecialIconStyle.PUPPY_MINIMAL && (isPrimaryAction || label == "↵" || label == "Go" || label == "Done") -> {
                    // Minimalist angled enter return arrow
                    Canvas(modifier = Modifier.size(26.dp)) {
                        val w = size.width
                        val h = size.height
                        val enterPath = Path().apply {
                            moveTo(w * 0.80f, h * 0.35f)
                            lineTo(w * 0.80f, h * 0.65f)
                            lineTo(w * 0.30f, h * 0.65f)
                            lineTo(w * 0.42f, h * 0.50f)
                            moveTo(w * 0.30f, h * 0.65f)
                            lineTo(w * 0.42f, h * 0.80f)
                        }
                        drawPath(path = enterPath, color = palette.textColor, style = Stroke(width = 2.2f))
                    }
                }

                // 3. STANDARD TYPOGRAPHY KEYS (All themes)
                else -> {
                    if (isSpaceBar) {
                        when (palette.spacebarStyle) {
                            SpacebarStyle.PUPPY_BRACKET -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Minimalist center bracket line: └─┘
                                    Canvas(modifier = Modifier.size(width = 38.dp, height = 12.dp)) {
                                        val w = size.width
                                        val h = size.height
                                        val bracketPath = Path().apply {
                                            moveTo(w * 0.1f, h * 0.2f)
                                            lineTo(w * 0.1f, h * 0.8f)
                                            lineTo(w * 0.9f, h * 0.8f)
                                            lineTo(w * 0.9f, h * 0.2f)
                                        }
                                        drawPath(path = bracketPath, color = palette.textColor.copy(alpha = 0.85f), style = Stroke(width = 2.0f))
                                    }

                                    // Watermark "nxv" or label
                                    Text(
                                        text = palette.spacebarWatermark ?: label,
                                        color = palette.secondaryTextColor.copy(alpha = 0.65f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(bottom = 3.dp, end = 8.dp)
                                    )
                                }
                            }
                            SpacebarStyle.STRAWBERRY_PILL -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (label != "SPACE") {
                                        Text(
                                            text = label,
                                            color = palette.secondaryTextColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                            SpacebarStyle.STANDARD_BAR -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = labelColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    // Subtle indicator bar
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 4.dp)
                                            .width(36.dp)
                                            .height(2.dp)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(palette.dividerColor)
                                    )
                                }
                            }
                        }
                    } else {
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
                    }
                }
            }

            // CapsLock active indicator bar
            if (isCapsLock && palette.specialIconStyle != ThemeSpecialIconStyle.STRAWBERRY_DESSERT) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 3.5.dp)
                        .width(14.dp)
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(palette.onAccentColor)
                )
            } else if (isShiftActive && palette.specialIconStyle == ThemeSpecialIconStyle.STANDARD) {
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

            // SubLabel (e.g. number hint or top symbol)
            if (showSubLabel && subLabel != null) {
                Text(
                    text = subLabel,
                    color = palette.topRowHintColor ?: palette.secondaryTextColor.copy(alpha = 0.6f),
                    fontSize = if (palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT) 9.sp else 9.sp,
                    fontWeight = if (palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .align(
                            if (palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT) Alignment.TopCenter
                            else Alignment.TopEnd
                        )
                        .padding(
                            top = if (palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT) 1.5.dp else 2.dp,
                            end = if (palette.specialIconStyle == ThemeSpecialIconStyle.STRAWBERRY_DESSERT) 0.dp else 4.dp
                        )
                )
            }
        }

        // Popup Preview when pressed based on popupMode
        if (isPressed && showPreview && popupMode != "disabled" && label.length == 1) {
            when {
                popupMode == "popup" && palette.popupStyle == KeyPopupStyle.PUPPY_CHARACTER && label[0].isLetter() -> {
                    PuppyPopupCharacter(
                        char = label.uppercase(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-56).dp)
                            .zIndex(99f)
                    )
                }
                popupMode == "popup" -> {
                    // Standard / Themed floating bubble
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-48).dp)
                            .size(46.dp)
                            .zIndex(99f)
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label.uppercase(),
                            color = palette.onAccentColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                popupMode == "mini" -> {
                    // Mini on-key indicator badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(palette.accentColor.copy(alpha = 0.85f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                            .zIndex(50f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label.uppercase(),
                            color = palette.onAccentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
