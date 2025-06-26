package com.gpi.desktopmode.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gpi.desktopmode.DockApp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState

@Composable
fun MacOSDock(
    onAppDrawerClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    
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
                onClick = onAppDrawerClick
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
    
    // Add hover detection for the entire dock
    val dockInteractionSource = remember { MutableInteractionSource() }
    val isDockAreaHovered by dockInteractionSource.collectIsHoveredAsState()
    
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
    
    // Calculate asymmetric padding based on hovered icon position
    val totalIcons = allDockApps.size
    val leftPadding by animateDpAsState(
        targetValue = if (isDockHovered && hoveredIndex >= 0) {
            val hoveredIconPosition = hoveredIndex.toFloat() / (totalIcons - 1)
            if (hoveredIconPosition <= 0.5f) {
                // Icon is in left half, expand more to the left
                basePadding + (hoveredPadding - basePadding) * (1f - hoveredIconPosition * 2f)
            } else {
                // Icon is in right half, expand less to the left
                basePadding
            }
        } else {
            basePadding
        },
        animationSpec = tween(durationMillis = 300),
        label = "leftPadding"
    )
    
    val rightPadding by animateDpAsState(
        targetValue = if (isDockHovered && hoveredIndex >= 0) {
            val hoveredIconPosition = hoveredIndex.toFloat() / (totalIcons - 1)
            if (hoveredIconPosition >= 0.5f) {
                // Icon is in right half, expand more to the right
                basePadding + (hoveredPadding - basePadding) * (hoveredIconPosition * 2f - 1f)
            } else {
                // Icon is in left half, expand less to the right
                basePadding
            }
        } else {
            basePadding
        },
        animationSpec = tween(durationMillis = 300),
        label = "rightPadding"
    )
    
    // Update dock hover state based on dock area hover
    LaunchedEffect(isDockAreaHovered) {
        isDockHovered = isDockAreaHovered
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Dock background with glass effect
        Box(
            modifier = Modifier
                .height(70.dp)
                .wrapContentWidth()
                .padding(horizontal = 8.dp)
                .hoverable(dockInteractionSource)
        ) {
            // No background image, just a frosted overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.20f))
            )
            // Dock icons
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .padding(start = leftPadding, end = rightPadding),
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
                                // Don't reset isDockHovered here - let the dock area handle it
                            }
                        )
                    }
                }
            }
        }
    }
} 