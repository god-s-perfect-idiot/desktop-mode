package com.gpi.desktopmode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable

data class DockApp(
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val onClick: () -> Unit,
    val isRecent: Boolean = false,
    val isRunning: Boolean = false
)

fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
} 