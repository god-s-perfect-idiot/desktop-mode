package com.gpi.desktopmode.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import com.gpi.desktopmode.R

fun getLocalBackgroundBitmap(context: Context): Bitmap? {
    return try {
        val inputStream = context.resources.openRawResource(R.raw.bg)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        android.util.Log.d("WallpaperUtil", "Local background loaded: ${bitmap.width}x${bitmap.height}")
        bitmap
    } catch (e: Exception) {
        android.util.Log.e("WallpaperUtil", "Error loading local background: ${e.message}")
        null
    }
}

fun blurBitmap(bitmap: Bitmap, radius: Int = 32): Bitmap {
    // Stack blur algorithm (simple, not as good as RenderEffect)
    val blurredBitmap = bitmap.copy(bitmap.config, true)
    if (radius < 1) return blurredBitmap
    
    val w = blurredBitmap.width
    val h = blurredBitmap.height
    val pix = IntArray(w * h)
    blurredBitmap.getPixels(pix, 0, w, 0, 0, w, h)
    
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
    blurredBitmap.setPixels(pix, 0, w, 0, 0, w, h)
    return blurredBitmap
}

@Composable
fun WallpaperBackground(
    overlayColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.2f)
) {
    val context = LocalContext.current
    var backgroundBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Load local background when the composable is first created
    LaunchedEffect(context) {
        try {
            val bitmap = getLocalBackgroundBitmap(context)
            android.util.Log.d("WallpaperBackground", "Local background loaded: ${bitmap != null}")
            backgroundBitmap = bitmap
        } catch (e: Exception) {
            android.util.Log.e("WallpaperBackground", "Error loading local background: ${e.message}")
        }
    }
    
    if (backgroundBitmap != null) {
        Image(
            bitmap = backgroundBitmap!!.asImageBitmap(),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )
    } else {
        // fallback: gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color(0xFF2C3E50),
                            androidx.compose.ui.graphics.Color(0xFF34495E),
                            androidx.compose.ui.graphics.Color(0xFF1A252F)
                        )
                    )
                )
        )
    }
}

@Composable
fun BlurredWallpaperBackground(
    blurRadius: Int = 32,
    overlayColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)
) {
    val context = LocalContext.current
    var backgroundBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var blurredBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Load local background when the composable is first created
    LaunchedEffect(context) {
        try {
            val bitmap = getLocalBackgroundBitmap(context)
            android.util.Log.d("BlurredWallpaperBackground", "Local background loaded: ${bitmap != null}")
            backgroundBitmap = bitmap
            
            // Blur the background if it was loaded successfully
            bitmap?.let { 
                try {
                    val blurred = blurBitmap(it, blurRadius)
                    android.util.Log.d("BlurredWallpaperBackground", "Background blurred successfully")
                    blurredBitmap = blurred
                } catch (e: Exception) {
                    android.util.Log.e("BlurredWallpaperBackground", "Error blurring background: ${e.message}")
                    // Use original bitmap if blurring fails
                    blurredBitmap = bitmap
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("BlurredWallpaperBackground", "Error loading local background: ${e.message}")
        }
    }
    
    if (blurredBitmap != null) {
        Image(
            bitmap = blurredBitmap!!.asImageBitmap(),
            contentDescription = "Blurred Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )
    } else {
        // fallback: gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color(0xFF2C3E50),
                            androidx.compose.ui.graphics.Color(0xFF34495E),
                            androidx.compose.ui.graphics.Color(0xFF1A252F)
                        )
                    )
                )
        )
    }
} 