package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwitchVideo
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.media.KeyboardVideoOverlayManager
import com.example.theme.KeyboardPalette

/**
 * Modern floating control dialog for in-keyboard video background overlay.
 * Controls video playback, opacity, key transparency, and audio muting.
 */
@Composable
fun VideoOverlayControlDialog(
    overlayManager: KeyboardVideoOverlayManager,
    palette: KeyboardPalette,
    onPickGalleryVideo: () -> Unit,
    onOpenDeviceMediaBrowser: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOverlayEnabled by overlayManager.isOverlayEnabled.collectAsState()
    val isPlaying by overlayManager.isPlaying.collectAsState()
    val isMuted by overlayManager.isMuted.collectAsState()
    val videoOpacity by overlayManager.videoOpacity.collectAsState()
    val keyTransparency by overlayManager.keyTransparency.collectAsState()
    val dimOverlay by overlayManager.dimOverlay.collectAsState()
    val activeTitle by overlayManager.activeVideoTitle.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(18.dp))
                .background(palette.suggestionBarBackground)
                .border(1.dp, palette.accentColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                .padding(16.dp)
                .testTag("video_overlay_control_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(palette.accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwitchVideo,
                                contentDescription = null,
                                tint = palette.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Video Overlay & Typing",
                                color = palette.textColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (activeTitle.isNotBlank()) activeTitle else "Background Video Mode",
                                color = palette.secondaryTextColor,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(palette.keyBackground)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = palette.textColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Master Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.keyBackground)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Enable Video Background",
                            color = palette.textColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Play video beneath typing keys",
                            color = palette.secondaryTextColor,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isOverlayEnabled,
                        onCheckedChange = { overlayManager.setOverlayEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = palette.accentColor
                        )
                    )
                }

                // Playback and Audio Quick Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Play/Pause Button
                    Button(
                        onClick = { overlayManager.togglePlayPause() },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) palette.accentColor else palette.keyBackground
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isPlaying) Color.White else palette.textColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isPlaying) "Playing" else "Paused",
                                fontSize = 12.sp,
                                color = if (isPlaying) Color.White else palette.textColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Mute/Unmute Audio Button
                    Button(
                        onClick = { overlayManager.toggleMute() },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isMuted) palette.accentColor else palette.keyBackground
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = if (!isMuted) Color.White else palette.textColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isMuted) "Muted" else "Audio ON",
                                fontSize = 12.sp,
                                color = if (!isMuted) Color.White else palette.textColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Slider 1: Video Brightness / Opacity
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Video Opacity",
                            color = palette.textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(videoOpacity * 100).toInt()}%",
                            color = palette.accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = videoOpacity,
                        onValueChange = { overlayManager.setVideoOpacity(it) },
                        valueRange = 0.10f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = palette.accentColor,
                            activeTrackColor = palette.accentColor
                        )
                    )
                }

                // Slider 2: Key Transparency (glass effect)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Key Transparency (See-Through Keys)",
                            color = palette.textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(keyTransparency * 100).toInt()}%",
                            color = palette.accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = keyTransparency,
                        onValueChange = { overlayManager.setKeyTransparency(it) },
                        valueRange = 0.0f..0.95f,
                        colors = SliderDefaults.colors(
                            thumbColor = palette.accentColor,
                            activeTrackColor = palette.accentColor
                        )
                    )
                }

                // Slider 3: Dark Scrim Tint (Contrast protection)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Dark Contrast Tint (Letter Readability)",
                            color = palette.textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(dimOverlay * 100).toInt()}%",
                            color = palette.accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = dimOverlay,
                        onValueChange = { overlayManager.setDimOverlay(it) },
                        valueRange = 0.0f..0.85f,
                        colors = SliderDefaults.colors(
                            thumbColor = palette.accentColor,
                            activeTrackColor = palette.accentColor
                        )
                    )
                }

                // Video Source Selection Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pick from Gallery
                    Button(
                        onClick = {
                            onDismiss()
                            onPickGalleryVideo()
                        },
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = palette.accentColor)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Pick Gallery", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Browse Device / Downloads
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenDeviceMediaBrowser()
                        },
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = palette.textColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Storage Videos", fontSize = 11.sp, color = palette.textColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Remove / Clear Overlay Button
                if (activeTitle.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                overlayManager.clearOverlay()
                                onDismiss()
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Remove Current Video Overlay",
                            color = Color(0xFFE57373),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
