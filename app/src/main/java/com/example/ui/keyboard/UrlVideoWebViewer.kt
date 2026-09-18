package com.example.ui.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwitchVideo
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.theme.KeyboardPalette
import java.util.Locale

/**
 * In-Keyboard URL Video Player and Web Viewer.
 * Directly plays video links from Copypad/Clipboard (YouTube, TikTok, Facebook,
 * direct mp4/m3u8, or web pages) right inside the keyboard.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun UrlVideoWebViewer(
    initialUrl: String,
    palette: KeyboardPalette,
    onClose: () -> Unit,
    onSetAsOverlayBackground: ((Uri, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentUrl by remember { mutableStateOf(formatUrl(initialUrl)) }
    var inputUrlText by remember { mutableStateOf(formatUrl(initialUrl)) }
    var isLoading by remember { mutableStateOf(true) }
    var webProgress by remember { mutableFloatStateOf(0f) }
    var pageTitle by remember { mutableStateOf("Loading Video...") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var directVideoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var isDirectVideoPlaying by remember { mutableStateOf(true) }

    val isDirectVideo = remember(currentUrl) {
        val lower = currentUrl.lowercase(Locale.ROOT)
        lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mkv") ||
                lower.endsWith(".3gp") || lower.endsWith(".m3u8") || lower.contains(".mp4?")
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                webViewRef?.destroy()
            } catch (_: Exception) {}
            webViewRef = null

            try {
                directVideoViewRef?.stopPlayback()
            } catch (_: Exception) {}
            directVideoViewRef = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .background(palette.keyboardBackground)
            .testTag("in_keyboard_url_video_viewer")
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(palette.suggestionBarBackground)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Back to Typing Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.keyBackground)
                    .clickable { onClose() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("url_viewer_back_typing")
            ) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "Back",
                    tint = palette.accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Typing",
                    color = palette.textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // URL input address bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.keyBackground)
                    .border(0.8.dp, palette.keyBorderColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = inputUrlText,
                    onValueChange = { inputUrlText = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = palette.textColor,
                        fontSize = 11.sp
                    ),
                    cursorBrush = SolidColor(palette.accentColor),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            val formatted = formatUrl(inputUrlText)
                            currentUrl = formatted
                            webViewRef?.loadUrl(formatted)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("url_viewer_input_bar")
                )
            }

            // Reload Button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(palette.keyBackground)
                    .clickable {
                        if (isDirectVideo) {
                            directVideoViewRef?.resume()
                        } else {
                            webViewRef?.reload()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload",
                    tint = palette.textColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Set as Video Overlay Background (if direct video or supported)
            if (onSetAsOverlayBackground != null && isDirectVideo) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(palette.accentColor)
                        .clickable {
                            try {
                                val uri = Uri.parse(currentUrl)
                                onSetAsOverlayBackground(uri, pageTitle)
                            } catch (_: Exception) {}
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwitchVideo,
                        contentDescription = "Set as Keyboard Background",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Open in External Browser
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(palette.keyBackground)
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open in Chrome/Browser",
                    tint = palette.textColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Close Button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(palette.keyBackground)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = palette.textColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Web Loading Progress Bar
        if (isLoading && webProgress < 1f) {
            LinearProgressIndicator(
                progress = { webProgress },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = palette.accentColor,
                trackColor = Color.Transparent
            )
        }

        // Player / Web View Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF0F0F0F)),
            contentAlignment = Alignment.Center
        ) {
            if (isDirectVideo) {
                // Direct Video Player View
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setVideoURI(Uri.parse(currentUrl))
                            setOnPreparedListener { mp ->
                                mp.isLooping = true
                                start()
                                isDirectVideoPlaying = true
                                isLoading = false
                            }
                            setOnErrorListener { _, _, _ ->
                                isLoading = false
                                false
                            }
                            directVideoViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Direct video play/pause tap overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            directVideoViewRef?.let { vv ->
                                if (vv.isPlaying) {
                                    vv.pause()
                                    isDirectVideoPlaying = false
                                } else {
                                    vv.start()
                                    isDirectVideoPlaying = true
                                }
                            }
                        }
                )
            } else {
                // Web Video Browser View (YouTube, TikTok, Facebook, HTML5 video)
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                mediaPlaybackRequiresUserGesture = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                            }
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    webProgress = newProgress / 100f
                                    isLoading = newProgress < 100
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    if (!title.isNullOrBlank()) {
                                        pageTitle = title
                                    }
                                }
                            }
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                    url?.let { inputUrlText = it }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                    url?.let { inputUrlText = it }
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val uri = request?.url ?: return false
                                    val scheme = uri.scheme?.lowercase(Locale.ROOT)
                                    if (scheme == "http" || scheme == "https") {
                                        return false // Load inside WebView
                                    }
                                    return try {
                                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        ctx.startActivity(intent)
                                        true
                                    } catch (_: Exception) {
                                        true
                                    }
                                }
                            }
                            loadUrl(currentUrl)
                            webViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isLoading && webProgress < 0.15f) {
                CircularProgressIndicator(
                    color = palette.accentColor,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

private fun formatUrl(url: String): String {
    val trimmed = url.trim()
    if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
        return trimmed
    }
    return "https://$trimmed"
}
