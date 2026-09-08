package com.example.ui.keyboard

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.KeyboardVideoOverlayManager
import com.example.theme.KeyboardPalette
import java.util.Locale

/**
 * Modern In-Keyboard Video Player Layout.
 * Features:
 * - Direct video rendering with sound inside the keyboard
 * - Play / Pause, 10s Forward, 10s Backward
 * - Interactive Timeline Seek Bar with Current Position & Total Duration
 * - Volume control (0% to 100%)
 * - Audio/Video mode switcher (play as audio or video)
 * - "Type Over Video" action (floating letters mode without key button backgrounds)
 * - Magic key shortcuts guide (Pe+space=pause, Pl+space=play, Vl<0-100>+space=vol, Cl+space=close)
 */
@Composable
fun KeyboardVideoPlayerLayout(
    overlayManager: KeyboardVideoOverlayManager,
    palette: KeyboardPalette,
    onStartTypingOverVideo: () -> Unit,
    onSwitchToAudioMode: ((filePath: String, title: String) -> Unit)? = null,
    onCloseVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUri by overlayManager.activeVideoUri.collectAsState()
    val activeTitle by overlayManager.activeVideoTitle.collectAsState()
    val isPlaying by overlayManager.isPlaying.collectAsState()
    val isMuted by overlayManager.isMuted.collectAsState()
    val volume by overlayManager.volume.collectAsState()
    val currentPosMs by overlayManager.currentPositionMs.collectAsState()
    val durationMs by overlayManager.durationMs.collectAsState()
    val seekToMs by overlayManager.seekToMs.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var showShortcutsInfo by remember { mutableStateOf(false) }
    var isSeeking by remember { mutableStateOf(false) }
    var sliderScrubPos by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(activeUri) {
        if (activeUri == null) {
            val demoDir = java.io.File(context.filesDir, "DemoMedia")
            if (demoDir.exists()) {
                val videoFiles = demoDir.listFiles()?.filter { 
                    val ext = it.extension.lowercase(Locale.ROOT)
                    ext in listOf("mp4", "mkv", "webm", "3gp") 
                }
                val firstVideo = videoFiles?.firstOrNull()
                if (firstVideo != null && firstVideo.exists()) {
                    val cleanTitle = firstVideo.nameWithoutExtension.replace("_", " ")
                    overlayManager.setVideoSource(Uri.fromFile(firstVideo), cleanTitle)
                }
            }
        }
    }

    fun formatTime(ms: Int): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format(Locale.US, "%d:%02d", min, sec)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color.Black)
    ) {
        // 1. Hardware Video Texture
        if (activeUri != null) {
            KeyboardVideoOverlayBackground(
                videoUri = activeUri!!,
                isPlaying = isPlaying,
                isMuted = isMuted,
                volume = volume,
                seekToMs = seekToMs,
                videoOpacity = 1.0f,
                dimOverlay = 0.25f,
                onSeekHandled = { overlayManager.onSeekHandled() },
                onPositionChanged = { pos, dur ->
                    overlayManager.updatePosition(pos, dur)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Translucent Gradient Overlay for Controls Legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.70f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // 3. Player UI Controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP HEADER BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title and Video badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(palette.accentColor.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = palette.accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = activeTitle.ifBlank { "Video Player" },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Top Actions: Audio/Video Switcher, Info, Close
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Switch to Audio Option
                    if (onSwitchToAudioMode != null && activeUri != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable {
                                    onSwitchToAudioMode(activeUri.toString(), activeTitle)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Audiotrack,
                                    contentDescription = "Play as Audio",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Audio Mode",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Magic Keys Info Button
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { showShortcutsInfo = !showShortcutsInfo },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Shortcuts Info",
                            tint = if (showShortcutsInfo) palette.accentColor else Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Close Video Button
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD32F2F).copy(alpha = 0.85f))
                            .clickable { onCloseVideo() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Video",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // --- MAGIC KEY SHORTCUTS DROPDOWN/CHIP ---
            AnimatedVisibility(
                visible = showShortcutsInfo,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .border(0.8.dp, palette.accentColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "⌨ Video Control Commands (Type while typing):",
                            color = palette.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• pe + Space = Pause  |  pl + Space = Play\n• vl50 + Space = Volume 50% (vl0 to vl100)\n• cl + Space = Close Video & Return to Keyboard",
                            color = Color.White,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // --- CENTER CONTROLS: -10s, Play/Pause, +10s ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 10s Rewind
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f))
                        .clickable { overlayManager.seekRelative(-10000) }
                        .testTag("video_player_rewind_10s"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10 Seconds",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Play / Pause (Large Central Button)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(palette.accentColor)
                        .clickable { overlayManager.togglePlayPause() }
                        .testTag("video_player_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // 10s Forward
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f))
                        .clickable { overlayManager.seekRelative(10000) }
                        .testTag("video_player_forward_10s"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10 Seconds",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // --- BOTTOM CONTROLS: TIMELINE & ACTIONS ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Timeline Seek Bar & Time Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val activePos = if (isSeeking) sliderScrubPos.toInt() else currentPosMs
                    Text(
                        text = formatTime(activePos),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Seek slider
                    val maxFloat = durationMs.coerceAtLeast(1).toFloat()
                    val currentFloat = (if (isSeeking) sliderScrubPos else currentPosMs.toFloat()).coerceIn(0f, maxFloat)

                    Slider(
                        value = currentFloat,
                        onValueChange = { newVal ->
                            isSeeking = true
                            sliderScrubPos = newVal
                        },
                        onValueChangeFinished = {
                            isSeeking = false
                            overlayManager.seekTo(sliderScrubPos.toInt())
                        },
                        valueRange = 0f..maxFloat,
                        colors = SliderDefaults.colors(
                            thumbColor = palette.accentColor,
                            activeTrackColor = palette.accentColor,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                            .testTag("video_player_timeline_slider")
                    )

                    Text(
                        text = formatTime(durationMs),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Bottom Action Row: Volume control + "Type over Video" Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Volume / Mute quick toggle & badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { overlayManager.toggleMute() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted || volume == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Mute/Unmute",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        val volPercent = if (isMuted) 0 else (volume * 100).toInt()
                        Text(
                            text = "$volPercent%",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Prominent "Type Over Video" Button
                    Button(
                        onClick = onStartTypingOverVideo,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.accentColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("video_player_type_over_video_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⌨ Type Over Video",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
