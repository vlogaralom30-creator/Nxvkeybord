package com.example.media

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Manages in-keyboard real-time Video Overlay and background video playback.
 * Allows playing gallery, downloaded, or social videos directly behind keyboard keys
 * with adjustable video opacity, key transparency, and audio muting while typing.
 */
class KeyboardVideoOverlayManager private constructor(private val appContext: Context) {

    private val prefs = appContext.getSharedPreferences("nxv_video_overlay_prefs", Context.MODE_PRIVATE)

    private val _isOverlayEnabled = MutableStateFlow(prefs.getBoolean("overlay_enabled", false))
    val isOverlayEnabled: StateFlow<Boolean> = _isOverlayEnabled.asStateFlow()

    private val _activeVideoUri = MutableStateFlow<Uri?>(
        prefs.getString("overlay_uri", null)?.let {
            try { Uri.parse(it) } catch (_: Exception) { null }
        }
    )
    val activeVideoUri: StateFlow<Uri?> = _activeVideoUri.asStateFlow()

    private val _activeVideoTitle = MutableStateFlow(prefs.getString("overlay_title", "") ?: "")
    val activeVideoTitle: StateFlow<String> = _activeVideoTitle.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isMuted = MutableStateFlow(prefs.getBoolean("overlay_muted", true))
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    // 0.1f to 1.0f (default 0.70f)
    private val _videoOpacity = MutableStateFlow(prefs.getFloat("overlay_opacity", 0.70f))
    val videoOpacity: StateFlow<Float> = _videoOpacity.asStateFlow()

    // 0.0f (solid) to 1.0f (full glass transparent), default 0.45f
    private val _keyTransparency = MutableStateFlow(prefs.getFloat("key_transparency", 0.45f))
    val keyTransparency: StateFlow<Float> = _keyTransparency.asStateFlow()

    // 0.0f to 0.85f (dark dimming scrim over video to maximize key legibility), default 0.25f
    private val _dimOverlay = MutableStateFlow(prefs.getFloat("dim_overlay", 0.25f))
    val dimOverlay: StateFlow<Float> = _dimOverlay.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    fun setVideoSource(uri: Uri, title: String, autoEnable: Boolean = true) {
        _activeVideoUri.value = uri
        val cleanTitle = title.ifBlank { "Keyboard Video" }
        _activeVideoTitle.value = cleanTitle
        if (autoEnable) {
            _isOverlayEnabled.value = true
        }
        _isPlaying.value = true

        prefs.edit()
            .putString("overlay_uri", uri.toString())
            .putString("overlay_title", cleanTitle)
            .putBoolean("overlay_enabled", _isOverlayEnabled.value)
            .apply()
    }

    fun setVideoFromFile(filePath: String, title: String, autoEnable: Boolean = true) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                setVideoSource(uri, title.ifBlank { file.nameWithoutExtension }, autoEnable)
            }
        } catch (e: Exception) {
            Log.e("VideoOverlayManager", "Failed to set video file: $filePath", e)
        }
    }

    fun toggleOverlayEnabled(): Boolean {
        val newState = !_isOverlayEnabled.value
        _isOverlayEnabled.value = newState
        prefs.edit().putBoolean("overlay_enabled", newState).apply()
        return newState
    }

    fun setOverlayEnabled(enabled: Boolean) {
        _isOverlayEnabled.value = enabled
        prefs.edit().putBoolean("overlay_enabled", enabled).apply()
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun toggleMute() {
        val newMute = !_isMuted.value
        _isMuted.value = newMute
        prefs.edit().putBoolean("overlay_muted", newMute).apply()
    }

    fun setMuted(muted: Boolean) {
        _isMuted.value = muted
        prefs.edit().putBoolean("overlay_muted", muted).apply()
    }

    fun setVideoOpacity(opacity: Float) {
        val clamped = opacity.coerceIn(0.10f, 1.0f)
        _videoOpacity.value = clamped
        prefs.edit().putFloat("overlay_opacity", clamped).apply()
    }

    fun setKeyTransparency(transparency: Float) {
        val clamped = transparency.coerceIn(0.0f, 0.95f)
        _keyTransparency.value = clamped
        prefs.edit().putFloat("key_transparency", clamped).apply()
    }

    fun setDimOverlay(dim: Float) {
        val clamped = dim.coerceIn(0.0f, 0.85f)
        _dimOverlay.value = clamped
        prefs.edit().putFloat("dim_overlay", clamped).apply()
    }

    fun updatePosition(posMs: Int, totalMs: Int) {
        _currentPositionMs.value = posMs
        if (totalMs > 0) {
            _durationMs.value = totalMs
        }
    }

    fun clearOverlay() {
        _isOverlayEnabled.value = false
        _activeVideoUri.value = null
        _activeVideoTitle.value = ""
        _isPlaying.value = false
        prefs.edit()
            .remove("overlay_uri")
            .remove("overlay_title")
            .putBoolean("overlay_enabled", false)
            .apply()
    }

    companion object {
        @Volatile
        private var instance: KeyboardVideoOverlayManager? = null

        fun getInstance(context: Context): KeyboardVideoOverlayManager {
            return instance ?: synchronized(this) {
                instance ?: KeyboardVideoOverlayManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
