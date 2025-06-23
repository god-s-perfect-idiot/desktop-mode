package com.gpi.desktopmode.ui

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.Modifier


fun getWallpaperBitmap(context: Context): Bitmap? {
    return try {
        val wallpaperDrawable = WallpaperManager.getInstance(context).drawable
        when (wallpaperDrawable) {
            is BitmapDrawable -> wallpaperDrawable.bitmap
            null -> null
            else -> wallpaperDrawable.toBitmap()
        }
    } catch (e: Exception) {
        null
    }
}

fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float = 32f): Bitmap {
    // Always use fastBlur for compatibility
    return fastBlur(bitmap, radius.toInt())
}

fun fastBlur(sentBitmap: Bitmap, radius: Int): Bitmap {
    // Stack blur algorithm (simple, not as good as RenderEffect)
    val bitmap = sentBitmap.copy(sentBitmap.config, true)
    if (radius < 1) return bitmap
    val w = bitmap.width
    val h = bitmap.height
    val pix = IntArray(w * h)
    bitmap.getPixels(pix, 0, w, 0, 0, w, h)
    var rsum: Int
    var gsum: Int
    var bsum: Int
    val div = radius + radius + 1
    val dv = IntArray(256 * div) { it / div }
    var yi = 0
    for (y in 0 until h) {
        rsum = 0; gsum = 0; bsum = 0
        for (i in -radius..radius) {
            val p = pix[yi + (i.coerceIn(0, w - 1))]
            rsum += (p shr 16) and 0xFF
            gsum += (p shr 8) and 0xFF
            bsum += p and 0xFF
        }
        for (x in 0 until w) {
            pix[yi + x] = (0xFF shl 24) or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
            val p1 = pix[yi + (x - radius).coerceIn(0, w - 1)]
            val p2 = pix[yi + (x + radius + 1).coerceIn(0, w - 1)]
            rsum += ((p2 shr 16) and 0xFF) - ((p1 shr 16) and 0xFF)
            gsum += ((p2 shr 8) and 0xFF) - ((p1 shr 8) and 0xFF)
            bsum += (p2 and 0xFF) - (p1 and 0xFF)
        }
        yi += w
    }
    bitmap.setPixels(pix, 0, w, 0, 0, w, h)
    return bitmap
}

@Composable
fun BlurredWallpaperBackground(
    blurRadius: Float = 32f,
    overlayColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)
) {
    val context = LocalContext.current
    val wallpaperBitmap = remember { getWallpaperBitmap(context) }
    val blurredBitmap = remember(wallpaperBitmap) {
        wallpaperBitmap?.let { blurBitmap(context, it, blurRadius) }
    }
    if (blurredBitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = blurredBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )
    } else {
        // fallback: just overlay color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )
    }
} 