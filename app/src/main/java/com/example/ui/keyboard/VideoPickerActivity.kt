package com.example.ui.keyboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Transparent helper activity to open the system Photo/Video picker or Gallery
 * from the IME keyboard service. Returns the chosen video Uri and filename.
 */
class VideoPickerActivity : ComponentActivity() {

    private val pickVisualMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        handleSelectedUri(uri)
    }

    private val getContentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        handleSelectedUri(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this)) {
                pickVisualMediaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
            } else {
                getContentLauncher.launch("video/*")
            }
        } catch (_: Exception) {
            try {
                getContentLauncher.launch("video/*")
            } catch (_: Exception) {
                onVideoPickedListener?.invoke(null, "")
                finish()
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
        }
    }

    private fun handleSelectedUri(uri: Uri?) {
        if (uri != null) {
            try {
                // Grant persistable permission if possible
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (_: Exception) {}

            var displayName = "Selected Video"
            try {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        displayName = cursor.getString(nameIndex) ?: "Selected Video"
                    }
                }
            } catch (_: Exception) {}

            onVideoPickedListener?.invoke(uri, displayName)
        } else {
            onVideoPickedListener?.invoke(null, "")
        }
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    companion object {
        var onVideoPickedListener: ((Uri?, String) -> Unit)? = null

        fun pickVideo(context: Context, onResult: (Uri?, String) -> Unit) {
            onVideoPickedListener = onResult
            val intent = Intent(context, VideoPickerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            context.startActivity(intent)
        }
    }
}
