package com.example.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * High-performance Asset Manager & Renderer for the "Free Fire Black Gold" Theme.
 *
 * Implements:
 * 1. Fast in-memory Bitmap cache to avoid repeatedly decoding PNG assets during recomposition.
 * 2. Transparent avatar rendering for mapped keys (A, B, C, D, E, F, H, K, M, etc.).
 * 3. Aspect-ratio preserving background rendering with adaptive crop and dark translucent overlay.
 * 4. Easily configurable key-to-avatar mapping.
 */
object FreeFireThemeAssetManager {

    // Configurable Key -> Avatar PNG mapping
    val avatarKeyMap: Map<String, String> = mapOf(
        "A" to "A.png",
        "B" to "B.png",
        "C" to "C.png",
        "D" to "D.png",
        "E" to "E.png",
        "F" to "F.png",
        "H" to "H.png", // Hayato
        "K" to "K.png", // Kelly
        "M" to "M.png"  // Maxim
    )

    private val bitmapCache = ConcurrentHashMap<String, Bitmap>()

    fun getAvatarAssetForChar(char: String): String? {
        val upper = char.uppercase()
        return avatarKeyMap[upper]
    }

    fun isAvatarKey(char: String): Boolean {
        return getAvatarAssetForChar(char) != null
    }

    /**
     * Efficiently loads and caches a bitmap from Assets or FilesDir.
     */
    suspend fun loadBitmap(context: Context, relativePath: String): Bitmap? {
        bitmapCache[relativePath]?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                // Try from assets in multiple potential folders
                val candidates = listOf(
                    relativePath,
                    "FreefireThemeAsset/$relativePath",
                    "themes/freefire_black_gold/avatars/$relativePath",
                    "themes/freefire_black_gold/background/$relativePath"
                )

                for (cand in candidates) {
                    try {
                        context.assets.open(cand).use { stream ->
                            val bmp = BitmapFactory.decodeStream(stream)
                            if (bmp != null) {
                                bitmapCache[relativePath] = bmp
                                return@withContext bmp
                            }
                        }
                    } catch (_: Exception) {}
                }

                // Also check app filesDir / DemoMedia
                val fileCandidates = listOf(
                    File(context.filesDir, "FreefireThemeAsset/$relativePath"),
                    File(context.filesDir, relativePath)
                )
                for (file in fileCandidates) {
                    if (file.exists() && file.length() > 0) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        if (bmp != null) {
                            bitmapCache[relativePath] = bmp
                            return@withContext bmp
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("FreeFireAssets", "Could not decode asset $relativePath", e)
            }
            null
        }
    }

    fun getCachedBitmap(relativePath: String): Bitmap? = bitmapCache[relativePath]
}

/**
 * Free Fire Background Layer:
 * - Fills entire keyboard canvas
 * - ContentScale.Crop adaptive scaling
 * - Adds a subtle dark translucent gradient overlay so keys & text remain crisp and 100% legible
 */
@Composable
fun FreeFireBackgroundLayer(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bgBitmap by remember { mutableStateOf(FreeFireThemeAssetManager.getCachedBitmap("background.png")) }

    LaunchedEffect(Unit) {
        if (bgBitmap == null) {
            bgBitmap = FreeFireThemeAssetManager.loadBitmap(context, "background.png")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (bgBitmap != null) {
            Image(
                bitmap = bgBitmap!!.asImageBitmap(),
                contentDescription = "Free Fire Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // High-grade fallback gaming gradient if image is still loading
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F1218),
                                Color(0xFF07090C),
                                Color(0xFF030405)
                            )
                        )
                    )
            )
        }

        // Dark translucent overlay for superior key contrast and readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x99080A0E),
                            Color(0xB305070A),
                            Color(0xCC030406)
                        )
                    )
                )
        )
    }
}

/**
 * Avatar Key Composable:
 * - Displays the transparent character PNG inside the key.
 * - Adds a subtle metallic gold outline and glow.
 * - Shows a tiny gold corner hint of the letter so user always knows the mapped key.
 */
@Composable
fun FreeFireAvatarKeyContent(
    char: String,
    isPressed: Boolean,
    keyHeight: Dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val assetName = FreeFireThemeAssetManager.getAvatarAssetForChar(char) ?: return
    var avatarBitmap by remember(assetName) {
        mutableStateOf(FreeFireThemeAssetManager.getCachedBitmap(assetName))
    }

    LaunchedEffect(assetName) {
        if (avatarBitmap == null) {
            avatarBitmap = FreeFireThemeAssetManager.loadBitmap(context, assetName)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (avatarBitmap != null) {
            // Subtle gold aura glow behind avatar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        val glowAlpha = if (isPressed) 0.45f else 0.18f
                        drawCircle(
                            color = Color(0xFFFFD700).copy(alpha = glowAlpha),
                            radius = (w * 0.45f).coerceAtLeast(h * 0.45f),
                            center = Offset(w * 0.5f, h * 0.5f)
                        )
                    }
            )

            // Transparent PNG Avatar
            Image(
                bitmap = avatarBitmap!!.asImageBitmap(),
                contentDescription = "Avatar $char",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            )
        } else {
            // Fallback letter rendering if loading
            Text(
                text = char.uppercase(),
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Small corner letter badge (Gold pill)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 2.dp, end = 3.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xCC101318))
                .border(0.5.dp, Color(0xAAFFD700), RoundedCornerShape(3.dp))
                .padding(horizontal = 2.5.dp, vertical = 0.5.dp)
        ) {
            Text(
                text = char.uppercase(),
                color = Color(0xFFFFD700),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
