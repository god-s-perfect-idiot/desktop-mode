package com.gpi.desktopmode

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpi.desktopmode.ui.theme.DesktopModeTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            DesktopModeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    MacOSDesktopScreen()
                }
            }
        }
    }
}

data class DockApp(
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val onClick: () -> Unit,
    val isRecent: Boolean = false
)

fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

@Composable
fun MacOSDesktopScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A), // Dark blue
                        Color(0xFF3B82F6), // Blue
                        Color(0xFF60A5FA)  // Light blue
                    )
                )
            )
    ) {
        // macOS Dock at the bottom
        MacOSDock()
    }
}

@Composable
fun MacOSDock() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    
    val pinnedApps = remember {
        listOf(
            DockApp(
                packageName = "com.android.launcher3",
                label = "App Drawer",
                icon = try {
                    packageManager.getApplicationIcon("com.android.launcher3")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.android.settings",
                label = "Settings",
                icon = try {
                    packageManager.getApplicationIcon("com.android.settings")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.google.android.calendar",
                label = "Calendar",
                icon = try {
                    packageManager.getApplicationIcon("com.google.android.calendar")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.android.vending",
                label = "Play Store",
                icon = try {
                    packageManager.getApplicationIcon("com.android.vending")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.whatsapp",
                label = "WhatsApp",
                icon = try {
                    packageManager.getApplicationIcon("com.whatsapp")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.facebook.katana",
                label = "Facebook",
                icon = try {
                    packageManager.getApplicationIcon("com.facebook.katana")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.instagram.android",
                label = "Instagram",
                icon = try {
                    packageManager.getApplicationIcon("com.instagram.android")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.google.android.youtube",
                label = "YouTube",
                icon = try {
                    packageManager.getApplicationIcon("com.google.android.youtube")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.google.android.gm",
                label = "Gmail",
                icon = try {
                    packageManager.getApplicationIcon("com.google.android.gm")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.google.android.apps.maps",
                label = "Maps",
                icon = try {
                    packageManager.getApplicationIcon("com.google.android.apps.maps")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.spotify.music",
                label = "Spotify",
                icon = try {
                    packageManager.getApplicationIcon("com.spotify.music")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.netflix.mediaclient",
                label = "Netflix",
                icon = try {
                    packageManager.getApplicationIcon("com.netflix.mediaclient")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            ),
            DockApp(
                packageName = "com.google.android.apps.photos",
                label = "Photos",
                icon = try {
                    packageManager.getApplicationIcon("com.google.android.apps.photos")
                } catch (e: Exception) {
                    null
                },
                onClick = { /* TODO */ }
            )
        )
    }
    
    val recentApps = remember {
        try {
            // Get all installed apps and pick some random ones as "recent"
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            
            // Filter out system apps and our own app
            val userApps = installedApps.filter { app ->
                app.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM == 0 &&
                app.packageName != "com.gpi.desktopmode" &&
                !pinnedApps.any { it.packageName == app.packageName }
            }
            
            // Take a few random apps as "recent"
            userApps.take(3).mapNotNull { app ->
                try {
                    android.util.Log.d("DockDebug", "Adding recent app: ${app.packageName}")
                    DockApp(
                        packageName = app.packageName,
                        label = app.loadLabel(packageManager).toString(),
                        icon = app.loadIcon(packageManager),
                        onClick = { 
                            // Launch the app
                            val intent = packageManager.getLaunchIntentForPackage(app.packageName)
                            intent?.let {
                                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(it)
                            }
                        },
                        isRecent = true
                    )
                } catch (e: Exception) {
                    android.util.Log.e("DockDebug", "Error loading app ${app.packageName}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DockDebug", "Error getting installed apps: ${e.message}")
            emptyList()
        }
    }
    
    val allDockApps = remember(pinnedApps, recentApps) {
        if (recentApps.isNotEmpty()) {
            pinnedApps + listOf(DockApp("", "separator", null, { }, true)) + recentApps
        } else {
            pinnedApps
        }
    }
    
    var hoveredIndex by remember { mutableStateOf(-1) }
    var isDockHovered by remember { mutableStateOf(false) }
    
    // Calculate dynamic spacing and width based on hover state
    val baseSpacing = 8.dp
    val hoveredSpacing = 20.dp
    val spacing by animateDpAsState(
        targetValue = if (isDockHovered) hoveredSpacing else baseSpacing,
        animationSpec = tween(durationMillis = 300),
        label = "spacing"
    )
    
    val basePadding = 8.dp
    val hoveredPadding = 24.dp
    val horizontalPadding by animateDpAsState(
        targetValue = if (isDockHovered) hoveredPadding else basePadding,
        animationSpec = tween(durationMillis = 300),
        label = "padding"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Dock background with glass effect
        Card(
            modifier = Modifier
                .height(70.dp)
                .wrapContentWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
            ) {
                // Dock icons
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .padding(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    allDockApps.forEachIndexed { index, app ->
                        if (app.packageName == "" && app.label == "separator") {
                            // Dash separator
                            Box(
                                modifier = Modifier
                                    .size(2.dp, 45.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.3f),
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                        } else {
                            DockIcon(
                                app = app,
                                onClick = app.onClick,
                                index = index,
                                hoveredIndex = hoveredIndex,
                                onHoverChanged = { isHovered ->
                                    hoveredIndex = if (isHovered) index else -1
                                    isDockHovered = isHovered
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DockIcon(
    app: DockApp,
    onClick: () -> Unit,
    index: Int,
    hoveredIndex: Int,
    onHoverChanged: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    // Calculate wave effect based on distance from hovered icon
    val distance = kotlin.math.abs(index - hoveredIndex)
    val waveEffect = when {
        hoveredIndex == -1 -> 0f // No hover
        distance == 0 -> 1f // Hovered icon
        distance == 1 -> 0.4f // Adjacent icons (reduced from 0.6f)
        distance == 2 -> 0.15f // Two icons away (reduced from 0.3f)
        else -> 0f // No effect
    }
    
    val scale by animateFloatAsState(
        targetValue = 1.0f + (waveEffect * 0.5f), // Scale up to 1.5x for hovered icon (increased from 1.3x)
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )
    
    // Update hover states
    LaunchedEffect(isHovered) {
        onHoverChanged(isHovered)
    }
    
    Box(
        modifier = Modifier
            .size(60.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.15f)
                    )
                )
            )
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (app.icon != null) {
            // Use actual app icon with rounded corners
            val bitmap = remember(app.icon) {
                app.icon.toBitmap()
            }
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = app.label,
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            // Fallback to material icon
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = app.label,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }
    }
}