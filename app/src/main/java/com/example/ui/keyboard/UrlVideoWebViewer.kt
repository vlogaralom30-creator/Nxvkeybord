package com.example.ui.keyboard

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwitchVideo
import androidx.compose.material.icons.filled.UnfoldMore
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.theme.KeyboardPalette
import java.util.Locale

/**
 * In-Keyboard Mini Desktop PC Browser & Media Monitor.
 *
 * Features:
 * 1. Minimal Floating Action Bar at the top of the URL preview browser for back, forward, refresh, home, zoom, and keyboard toggle.
 * 2. Dedicated Mini Keyboard for in-browser typing (searches, forms, URLs, and inputs).
 * 3. Forced Desktop PC Mode (Chrome Windows 10 User-Agent, 1280px wide viewport, and overview scaling).
 * 4. Expandable monitor height (280dp, 350dp, 420dp).
 * 5. Smart search omnibox with clipboard paste and bookmarks.
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

    // Normalize starting URL or default to Google home
    val startingUrl = remember(initialUrl) {
        val trimmed = initialUrl.trim()
        if (trimmed.isBlank() || trimmed == "https://" || trimmed == "http://") {
            "https://www.google.com"
        } else {
            formatUrlOrSearch(trimmed)
        }
    }

    var currentUrl by remember { mutableStateOf(startingUrl) }
    var inputUrlText by remember { mutableStateOf(startingUrl) }
    var isLoading by remember { mutableStateOf(true) }
    var webProgress by remember { mutableFloatStateOf(0f) }
    var pageTitle by remember { mutableStateOf("Desktop PC Browser") }
    var isDesktopMode by remember { mutableStateOf(true) }
    var zoomPercent by remember { mutableIntStateOf(60) } // Default 60% for crisp desktop monitor density
    var monitorHeightLevel by remember { mutableIntStateOf(1) } // 0: 290dp, 1: 360dp, 2: 430dp
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var isMiniKeyboardVisible by remember { mutableStateOf(false) }
    var isShiftActive by remember { mutableStateOf(false) }
    var isSymbolActive by remember { mutableStateOf(false) }
    var isOmniboxFocused by remember { mutableStateOf(false) }
    var showTopSearchBar by remember { mutableStateOf(true) }

    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var directVideoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var isDirectVideoPlaying by remember { mutableStateOf(true) }

    val isDirectVideo = remember(currentUrl) {
        val lower = currentUrl.lowercase(Locale.ROOT)
        lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mkv") ||
                lower.endsWith(".3gp") || lower.endsWith(".m3u8") || lower.contains(".mp4?")
    }

    // Dynamic animated height accommodating browser + optional mini keyboard
    val baseHeight = when (monitorHeightLevel) {
        0 -> 290.dp
        1 -> 360.dp
        else -> 430.dp
    }
    val extraKeyboardHeight = if (isMiniKeyboardVisible) 140.dp else 0.dp

    val animatedHeight by animateDpAsState(
        targetValue = baseHeight + extraKeyboardHeight,
        animationSpec = tween(durationMillis = 220),
        label = "monitor_height"
    )

    val desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    val mobileUserAgent = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

    // Apply Desktop / Mobile and zoom settings dynamically to WebView
    LaunchedEffect(isDesktopMode, zoomPercent, webViewRef) {
        webViewRef?.let { webView ->
            webView.settings.userAgentString = if (isDesktopMode) desktopUserAgent else mobileUserAgent
            webView.settings.textZoom = zoomPercent
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true
            webView.setInitialScale(if (isDesktopMode) zoomPercent else 100)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                webViewRef?.stopLoading()
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
            .height(animatedHeight)
            .background(Color(0xFF0C0E14))
            .border(1.dp, palette.accentColor.copy(alpha = 0.4f))
            .testTag("in_keyboard_desktop_browser")
    ) {
        // =====================================================================
        // 1. TOP WINDOW BAR & MINIMAL ACTION BAR
        // =====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1B1F2A),
                            Color(0xFF12151D)
                        )
                    )
                )
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Window control dots (Mac / PC Monitor Style)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFF5F56)).clickable { onClose() })
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFFBD2E)).clickable { monitorHeightLevel = (monitorHeightLevel + 1) % 3 })
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF27C93F)).clickable { isDesktopMode = !isDesktopMode })

                Spacer(modifier = Modifier.width(4.dp))

                // Monitor Title & SSL Lock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.widthIn(max = 130.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure",
                        tint = Color(0xFF4ADE80),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = pageTitle,
                        color = Color(0xFFE2E8F0),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Quick Action Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Mini Keyboard Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isMiniKeyboardVisible) palette.accentColor else Color(0xFF1E293B))
                        .clickable { isMiniKeyboardVisible = !isMiniKeyboardVisible }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("toggle_mini_keyboard_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = if (isMiniKeyboardVisible) Icons.Default.KeyboardHide else Icons.Default.Keyboard,
                            contentDescription = "Toggle Mini Keyboard",
                            tint = if (isMiniKeyboardVisible) Color.Black else Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = if (isMiniKeyboardVisible) "Hide Key" else "Mini Key",
                            color = if (isMiniKeyboardVisible) Color.Black else Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Desktop PC Mode Badge Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isDesktopMode) Color(0xFF2563EB) else Color(0xFF334155))
                        .clickable {
                            isDesktopMode = !isDesktopMode
                            webViewRef?.settings?.userAgentString = if (isDesktopMode) desktopUserAgent else mobileUserAgent
                            webViewRef?.reload()
                        }
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = if (isDesktopMode) Icons.Default.Computer else Icons.Default.Smartphone,
                            contentDescription = "Toggle Desktop Mode",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = if (isDesktopMode) "PC" else "Mobile",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Zoom - Button
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            zoomPercent = (zoomPercent - 10).coerceAtLeast(35)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(12.dp))
                }

                // Current Zoom Label
                Text(
                    text = "$zoomPercent%",
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 1.dp)
                )

                // Zoom + Button
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            zoomPercent = (zoomPercent + 10).coerceAtMost(150)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(12.dp))
                }

                // Expand / Collapse Height Toggle
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            monitorHeightLevel = (monitorHeightLevel + 1) % 3
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = "Expand Screen", tint = Color.White, modifier = Modifier.size(12.dp))
                }

                // Return to Main Keyboard
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF334155))
                        .clickable { onClose() }
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Browser",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // =====================================================================
        // 2. MINIMAL FLOATING ACTION BAR & OMNIBOX ADDRESS BAR
        // =====================================================================
        AnimatedVisibility(
            visible = showTopSearchBar,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(Color(0xFF141722))
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Back Button
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (canGoBack) Color(0xFF1E293B) else Color(0xFF0F172A))
                        .clickable(enabled = canGoBack) {
                            webViewRef?.let { if (it.canGoBack()) it.goBack() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (canGoBack) Color.White else Color(0xFF475569),
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Forward Button
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (canGoForward) Color(0xFF1E293B) else Color(0xFF0F172A))
                        .clickable(enabled = canGoForward) {
                            webViewRef?.let { if (it.canGoForward()) it.goForward() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (canGoForward) Color.White else Color(0xFF475569),
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Refresh Button
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            if (isDirectVideo) directVideoViewRef?.resume() else webViewRef?.reload()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Page",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Home Button (Google)
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            val homeUrl = "https://www.google.com"
                            currentUrl = homeUrl
                            inputUrlText = homeUrl
                            webViewRef?.loadUrl(homeUrl)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Smart Omnibox Address Input Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B0D13))
                        .border(0.8.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BasicTextField(
                            value = inputUrlText,
                            onValueChange = {
                                inputUrlText = it
                                isOmniboxFocused = true
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(palette.accentColor),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    val formatted = formatUrlOrSearch(inputUrlText)
                                    currentUrl = formatted
                                    inputUrlText = formatted
                                    webViewRef?.loadUrl(formatted)
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("desktop_browser_omnibox")
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            // Paste from Clipboard
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier
                                    .size(13.dp)
                                    .clickable {
                                        try {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = clipboard.primaryClip
                                            if (clip != null && clip.itemCount > 0) {
                                                val text = clip.getItemAt(0).text?.toString() ?: ""
                                                if (text.isNotBlank()) {
                                                    val formatted = formatUrlOrSearch(text)
                                                    inputUrlText = formatted
                                                    currentUrl = formatted
                                                    webViewRef?.loadUrl(formatted)
                                                }
                                            }
                                        } catch (_: Exception) {}
                                    }
                            )

                            // Go / Search Action Icon
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Go",
                                tint = palette.accentColor,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        val formatted = formatUrlOrSearch(inputUrlText)
                                        currentUrl = formatted
                                        inputUrlText = formatted
                                        webViewRef?.loadUrl(formatted)
                                    }
                            )
                        }
                    }
                }

                // Copy Current URL Button
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            try {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("URL", currentUrl))
                                Toast.makeText(context, "URL Copied: $currentUrl", Toast.LENGTH_SHORT).show()
                            } catch (_: Exception) {}
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy URL",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Open in External Chrome
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
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
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open in Chrome",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        // =====================================================================
        // 3. QUICK BOOKMARKS STRIP
        // =====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(Color(0xFF0F1118))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val bookmarks = listOf(
                "🔍 Google" to "https://www.google.com",
                "▶️ YouTube" to "https://www.youtube.com",
                "📘 Facebook" to "https://www.facebook.com",
                "📖 Wikipedia" to "https://www.wikipedia.org",
                "🤖 ChatGPT" to "https://chat.openai.com",
                "🐙 GitHub" to "https://www.github.com",
                "🔴 Reddit" to "https://www.reddit.com",
                "🎵 TikTok" to "https://www.tiktok.com",
                "📰 BBC News" to "https://www.bbc.com"
            )

            for ((name, url) in bookmarks) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF1B202E))
                        .clickable {
                            currentUrl = url
                            inputUrlText = url
                            webViewRef?.loadUrl(url)
                        }
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = name,
                        color = Color(0xFFCBD5E1),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Web Loading Progress Indicator
        if (isLoading && webProgress < 1f) {
            LinearProgressIndicator(
                progress = { webProgress },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = palette.accentColor,
                trackColor = Color.Transparent
            )
        }

        // =====================================================================
        // 4. MINI PC MONITOR VIEWPORT (With Floating Action Bar Overlay)
        // =====================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF000000))
                .border(1.dp, Color(0xFF1E293B)),
            contentAlignment = Alignment.TopCenter
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
                // Desktop Web Browser View
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
                                databaseEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                supportZoom()
                                builtInZoomControls = true
                                displayZoomControls = false
                                textZoom = zoomPercent
                                mediaPlaybackRequiresUserGesture = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                cacheMode = WebSettings.LOAD_DEFAULT
                                allowFileAccess = true
                                allowContentAccess = true
                                userAgentString = if (isDesktopMode) desktopUserAgent else mobileUserAgent
                            }
                            setInitialScale(if (isDesktopMode) zoomPercent else 100)

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
                                    url?.let {
                                        currentUrl = it
                                        inputUrlText = it
                                    }
                                    canGoBack = view?.canGoBack() ?: false
                                    canGoForward = view?.canGoForward() ?: false
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                    url?.let {
                                        currentUrl = it
                                        inputUrlText = it
                                    }
                                    canGoBack = view?.canGoBack() ?: false
                                    canGoForward = view?.canGoForward() ?: false

                                    // Inject desktop viewport meta tag to eliminate mobile sticky headers and large popups
                                    if (isDesktopMode) {
                                        view?.evaluateJavascript(
                                            """
                                            (function() {
                                                try {
                                                    var meta = document.querySelector('meta[name="viewport"]');
                                                    if (!meta) {
                                                        meta = document.createElement('meta');
                                                        meta.name = 'viewport';
                                                        document.head.appendChild(meta);
                                                    }
                                                    meta.content = 'width=1280, initial-scale=0.45, maximum-scale=3.0, user-scalable=yes';
                                                } catch(e) {}
                                            })();
                                            """.trimIndent(),
                                            null
                                        )
                                    }
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

            // Minimal Floating Action Bar over the Web Content
            MinimalFloatingBrowserActionBar(
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                isMiniKeyboardVisible = isMiniKeyboardVisible,
                onBack = { webViewRef?.let { if (it.canGoBack()) it.goBack() } },
                onForward = { webViewRef?.let { if (it.canGoForward()) it.goForward() } },
                onRefresh = { if (isDirectVideo) directVideoViewRef?.resume() else webViewRef?.reload() },
                onHome = {
                    val homeUrl = "https://www.google.com"
                    currentUrl = homeUrl
                    inputUrlText = homeUrl
                    webViewRef?.loadUrl(homeUrl)
                },
                onToggleMiniKeyboard = { isMiniKeyboardVisible = !isMiniKeyboardVisible },
                palette = palette,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .align(Alignment.TopCenter)
            )

            if (isLoading && webProgress < 0.15f) {
                CircularProgressIndicator(
                    color = palette.accentColor,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp)
                )
            }
        }

        // =====================================================================
        // 5. IN-BROWSER MINI KEYBOARD (For typing in web forms, search, & URLs)
        // =====================================================================
        AnimatedVisibility(
            visible = isMiniKeyboardVisible,
            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
        ) {
            MiniInBrowserKeyboard(
                palette = palette,
                isShiftActive = isShiftActive,
                isSymbolActive = isSymbolActive,
                onToggleShift = { isShiftActive = !isShiftActive },
                onToggleSymbols = { isSymbolActive = !isSymbolActive },
                onCharTyped = { char ->
                    if (isOmniboxFocused) {
                        inputUrlText += char
                    }
                    sendCharToWeb(char, webViewRef)
                },
                onDelete = {
                    if (isOmniboxFocused && inputUrlText.isNotEmpty()) {
                        inputUrlText = inputUrlText.dropLast(1)
                    }
                    sendBackspaceToWeb(webViewRef)
                },
                onSpace = {
                    if (isOmniboxFocused) {
                        inputUrlText += " "
                    }
                    sendCharToWeb(" ", webViewRef)
                },
                onEnter = {
                    if (isOmniboxFocused) {
                        val formatted = formatUrlOrSearch(inputUrlText)
                        currentUrl = formatted
                        inputUrlText = formatted
                        webViewRef?.loadUrl(formatted)
                    } else {
                        sendEnterToWeb(webViewRef)
                    }
                },
                onPaste = {
                    try {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = clipboard.primaryClip
                        if (clip != null && clip.itemCount > 0) {
                            val text = clip.getItemAt(0).text?.toString() ?: ""
                            if (text.isNotBlank()) {
                                if (isOmniboxFocused) {
                                    inputUrlText += text
                                }
                                for (ch in text) {
                                    sendCharToWeb(ch.toString(), webViewRef)
                                }
                            }
                        }
                    } catch (_: Exception) {}
                },
                onCloseKeyboard = { isMiniKeyboardVisible = false }
            )
        }

        // =====================================================================
        // 6. BOTTOM STATUS STRIP
        // =====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(Color(0xFF0A0C10))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val domain = remember(currentUrl) {
                try {
                    Uri.parse(currentUrl).host ?: "desktop-monitor"
                } catch (_: Exception) {
                    "desktop-monitor"
                }
            }

            Text(
                text = "🖥️ $domain",
                color = Color(0xFF64748B),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = if (isDesktopMode) "PC Mode (1280px) • Zoom $zoomPercent%" else "Mobile Mode • Zoom $zoomPercent%",
                color = if (isDesktopMode) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Minimal Floating Action Bar floating at the top of the URL preview browser.
 * Ensures the mini-browser navigation is manageable despite limited screen space.
 */
@Composable
private fun MinimalFloatingBrowserActionBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    isMiniKeyboardVisible: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onRefresh: () -> Unit,
    onHome: () -> Unit,
    onToggleMiniKeyboard: () -> Unit,
    palette: KeyboardPalette,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xE6131722))
            .border(1.dp, palette.accentColor.copy(alpha = 0.45f), CircleShape)
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .testTag("floating_browser_action_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Back
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (canGoBack) Color(0xFF1E293B) else Color(0x661E293B))
                .clickable(enabled = canGoBack) { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (canGoBack) Color.White else Color(0xFF64748B),
                modifier = Modifier.size(13.dp)
            )
        }

        // Forward
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (canGoForward) Color(0xFF1E293B) else Color(0x661E293B))
                .clickable(enabled = canGoForward) { onForward() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Forward",
                tint = if (canGoForward) Color.White else Color(0xFF64748B),
                modifier = Modifier.size(13.dp)
            )
        }

        // Refresh / Reload
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .clickable { onRefresh() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Page",
                tint = palette.accentColor,
                modifier = Modifier.size(13.dp)
            )
        }

        // Home
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .clickable { onHome() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .height(14.dp)
                .width(1.dp)
                .background(Color(0xFF334155))
        )

        // Mini Keyboard Button
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isMiniKeyboardVisible) palette.accentColor else Color(0xFF2563EB))
                .clickable { onToggleMiniKeyboard() }
                .padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Keyboard,
                contentDescription = "Mini Keyboard",
                tint = if (isMiniKeyboardVisible) Color.Black else Color.White,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (isMiniKeyboardVisible) "Typing" else "Type",
                color = if (isMiniKeyboardVisible) Color.Black else Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Dedicated In-Browser Mini Keyboard for typing directly into search bars,
 * login fields, and web page inputs without leaving the mini desktop browser!
 */
@Composable
private fun MiniInBrowserKeyboard(
    palette: KeyboardPalette,
    isShiftActive: Boolean,
    isSymbolActive: Boolean,
    onToggleShift: () -> Unit,
    onToggleSymbols: () -> Unit,
    onCharTyped: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onPaste: () -> Unit,
    onCloseKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val row1 = if (isSymbolActive) {
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    } else {
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    }

    val row2 = if (isSymbolActive) {
        listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/")
    } else {
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    }

    val row3 = if (isSymbolActive) {
        listOf("*", "\"", "'", ":", ";", "!", "?")
    } else {
        listOf("z", "x", "c", "v", "b", "n", "m")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF10131C))
            .border(width = 0.8.dp, color = Color(0xFF1E293B))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Row 0: Quick web shortcut pills (.com, /, @, -, ?, paste, hide)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val quickChips = listOf(".com", "www.", ".org", "/", "-", "?", "@")
            quickChips.forEach { chip ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF1E2433))
                        .clickable { onCharTyped(chip) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chip,
                        color = Color(0xFF93C5FD),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Paste
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF334155))
                    .clickable { onPaste() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📋 Paste",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Hide keyboard
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF475569))
                    .clickable { onCloseKeyboard() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardHide,
                    contentDescription = "Hide Mini Keyboard",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Row 1: Q W E R T Y U I O P
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.5.dp)
        ) {
            row1.forEach { char ->
                val displayText = if (isShiftActive && !isSymbolActive) char.uppercase() else char
                MiniKeyButton(
                    label = displayText,
                    modifier = Modifier.weight(1f),
                    palette = palette,
                    onClick = { onCharTyped(displayText) }
                )
            }
        }

        // Row 2: A S D F G H J K L
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.5.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            row2.forEach { char ->
                val displayText = if (isShiftActive && !isSymbolActive) char.uppercase() else char
                MiniKeyButton(
                    label = displayText,
                    modifier = Modifier.weight(1f),
                    palette = palette,
                    onClick = { onCharTyped(displayText) }
                )
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

        // Row 3: [Shift] Z X C V B N M [Backspace]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isShiftActive) palette.accentColor else Color(0xFF1F2536))
                    .clickable { onToggleShift() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Shift",
                    tint = if (isShiftActive) Color.Black else Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }

            row3.forEach { char ->
                val displayText = if (isShiftActive && !isSymbolActive) char.uppercase() else char
                MiniKeyButton(
                    label = displayText,
                    modifier = Modifier.weight(1f),
                    palette = palette,
                    onClick = { onCharTyped(displayText) }
                )
            }

            // Backspace
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF2E1A1A))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌫",
                    color = Color(0xFFF87171),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Row 4: [123 / ABC] [ . ] [ Spacebar ] [ Enter / Search ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 123 / ABC Switch
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1F2536))
                    .clickable { onToggleSymbols() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSymbolActive) "ABC" else "123",
                    color = Color.White,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Dot
            MiniKeyButton(
                label = ".",
                modifier = Modifier.weight(1f),
                palette = palette,
                onClick = { onCharTyped(".") }
            )

            // Spacebar
            Box(
                modifier = Modifier
                    .weight(4.2f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E2433))
                    .border(0.5.dp, Color(0xFF334155), RoundedCornerShape(4.dp))
                    .clickable { onSpace() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "space",
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Enter / Search
            Box(
                modifier = Modifier
                    .weight(1.8f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(palette.accentColor)
                    .clickable { onEnter() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search ↵",
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MiniKeyButton(
    label: String,
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF1B202D))
            .border(0.5.dp, Color(0xFF2A3347), RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Dispatches a typed character to active HTML input element on the webpage.
 */
private fun sendCharToWeb(char: String, webView: WebView?) {
    val escaped = char.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "\\n")
    webView?.evaluateJavascript(
        """
        (function() {
            try {
                var el = document.activeElement;
                if (el && (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA' || el.isContentEditable)) {
                    if (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA') {
                        var start = el.selectionStart || el.value.length;
                        var end = el.selectionEnd || el.value.length;
                        var val = el.value || '';
                        el.value = val.substring(0, start) + '$escaped' + val.substring(end);
                        el.selectionStart = el.selectionEnd = start + $escaped.length;
                        el.dispatchEvent(new Event('input', { bubbles: true }));
                        el.dispatchEvent(new Event('change', { bubbles: true }));
                    } else {
                        document.execCommand('insertText', false, '$escaped');
                    }
                }
            } catch(e) {}
        })();
        """.trimIndent(),
        null
    )
}

/**
 * Dispatches a backspace deletion to the active HTML input element on the webpage.
 */
private fun sendBackspaceToWeb(webView: WebView?) {
    webView?.evaluateJavascript(
        """
        (function() {
            try {
                var el = document.activeElement;
                if (el && (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA')) {
                    var start = el.selectionStart || 0;
                    var end = el.selectionEnd || 0;
                    var val = el.value || '';
                    if (start > 0 && start === end) {
                        el.value = val.substring(0, start - 1) + val.substring(end);
                        el.selectionStart = el.selectionEnd = start - 1;
                        el.dispatchEvent(new Event('input', { bubbles: true }));
                        el.dispatchEvent(new Event('change', { bubbles: true }));
                    } else if (start !== end) {
                        el.value = val.substring(0, start) + val.substring(end);
                        el.selectionStart = el.selectionEnd = start;
                        el.dispatchEvent(new Event('input', { bubbles: true }));
                        el.dispatchEvent(new Event('change', { bubbles: true }));
                    }
                } else {
                    document.execCommand('delete', false, null);
                }
            } catch(e) {}
        })();
        """.trimIndent(),
        null
    )
}

/**
 * Dispatches Enter / Form submit to the active HTML element on the webpage.
 */
private fun sendEnterToWeb(webView: WebView?) {
    webView?.evaluateJavascript(
        """
        (function() {
            try {
                var el = document.activeElement;
                if (el) {
                    el.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', keyCode: 13, which: 13, bubbles: true }));
                    el.dispatchEvent(new KeyboardEvent('keypress', { key: 'Enter', keyCode: 13, which: 13, bubbles: true }));
                    el.dispatchEvent(new KeyboardEvent('keyup', { key: 'Enter', keyCode: 13, which: 13, bubbles: true }));
                    if (el.form) el.form.submit();
                }
            } catch(e) {}
        })();
        """.trimIndent(),
        null
    )
}

/**
 * Parses user input into a valid web URL or a Google search URL.
 */
private fun formatUrlOrSearch(input: String): String {
    val trimmed = input.trim()
    if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
        return trimmed
    }
    // If user entered a domain like "google.com" or "youtube.com/watch"
    if (trimmed.contains(".") && !trimmed.contains(" ")) {
        return "https://$trimmed"
    }
    // Otherwise treat as a search query
    val encoded = Uri.encode(trimmed)
    return "https://www.google.com/search?q=$encoded"
}
