package com.example.ui.keyboard

import android.graphics.SurfaceTexture
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Renders an active background video overlay behind the keyboard keys.
 * Plays smoothly and continuously on a hardware-accelerated TextureView with
 * custom opacity, audio mute/unmute, and contrast dimming scrim so keys and
 * letters remain 100% visible, touchable, and readable.
 */
@Composable
fun KeyboardVideoOverlayBackground(
    videoUri: Uri,
    isPlaying: Boolean,
    isMuted: Boolean = false,
    volume: Float = 1.0f,
    seekToMs: Int? = null,
    videoOpacity: Float,
    dimOverlay: Float,
    onSeekHandled: () -> Unit = {},
    onPositionChanged: (posMs: Int, durationMs: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var surfaceTextureRef by remember { mutableStateOf<SurfaceTexture?>(null) }
    var isPrepared by remember { mutableStateOf(false) }

    // Sync volume with isMuted & volume float
    LaunchedEffect(isMuted, volume, mediaPlayer) {
        mediaPlayer?.let { mp ->
            try {
                val vol = if (isMuted) 0f else volume.coerceIn(0f, 1f)
                mp.setVolume(vol, vol)
            } catch (_: Exception) {}
        }
    }

    // Sync seek target
    LaunchedEffect(seekToMs, isPrepared, mediaPlayer) {
        val target = seekToMs
        if (target != null && isPrepared) {
            mediaPlayer?.let { mp ->
                try {
                    mp.seekTo(target)
                } catch (_: Exception) {}
            }
            onSeekHandled()
        }
    }

    // Sync play / pause state
    LaunchedEffect(isPlaying, isPrepared, mediaPlayer) {
        mediaPlayer?.let { mp ->
            try {
                if (isPrepared) {
                    if (isPlaying && !mp.isPlaying) {
                        mp.start()
                    } else if (!isPlaying && mp.isPlaying) {
                        mp.pause()
                    }
                }
            } catch (_: Exception) {}
        }
    }

    DisposableEffect(videoUri) {
        onDispose {
            try {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) mp.stop()
                    mp.release()
                }
            } catch (_: Exception) {}
            mediaPlayer = null
            isPrepared = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Hardware Video Texture
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureAvailable(
                            surfaceTexture: SurfaceTexture,
                            width: Int,
                            height: Int
                        ) {
                            surfaceTextureRef = surfaceTexture
                            try {
                                mediaPlayer?.release()
                                val mp = MediaPlayer().apply {
                                    setAudioAttributes(
                                        AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .build()
                                    )
                                    setSurface(Surface(surfaceTexture))
                                    setDataSource(ctx, videoUri)
                                    isLooping = true
                                    val vol = if (isMuted) 0f else 1.0f
                                    setVolume(vol, vol)
                                    setOnPreparedListener { player ->
                                        isPrepared = true
                                        onPositionChanged(player.currentPosition, player.duration)
                                        if (isPlaying) {
                                            player.start()
                                        }
                                    }
                                    prepareAsync()
                                }
                                mediaPlayer = mp
                            } catch (e: Exception) {
                                android.util.Log.e("VideoOverlay", "TextureView error preparing video", e)
                            }
                        }

                        override fun onSurfaceTextureSizeChanged(
                            surfaceTexture: SurfaceTexture,
                            width: Int,
                            height: Int
                        ) {}

                        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                            try {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                            } catch (_: Exception) {}
                            mediaPlayer = null
                            isPrepared = false
                            surfaceTextureRef = null
                            return true
                        }

                        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
                            mediaPlayer?.let { mp ->
                                try {
                                    if (mp.isPlaying) {
                                        onPositionChanged(mp.currentPosition, mp.duration)
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .alpha(videoOpacity.coerceIn(0.1f, 1.0f))
        )

        // 2. Adaptive Dimming & Contrast Scrim
        // Protects legibility of white/black keyboard key symbols over bright videos
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = (dimOverlay + 0.15f).coerceAtMost(0.92f)),
                            Color.Black.copy(alpha = dimOverlay.coerceAtMost(0.85f)),
                            Color.Black.copy(alpha = (dimOverlay + 0.10f).coerceAtMost(0.90f))
                        )
                    )
                )
        )
    }
}
