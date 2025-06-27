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
import com.gpi.desktopmode.RunningAppsManager
import com.gpi.desktopmode.WindowManagerHelper
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
import android.util.Log

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
                onClick = { 
                    WindowManagerHelper.launchAppInWindow(context, "com.android.settings", "Settings")
                }
            ),
            DockApp(
                packageName = "com.google.android.calendar",
                label = "Calendar",
                icon = try {
                    packageManager.getApplicationIcon("com.google.android.calendar")
                } catch (e: Exception) {
                    null
                },
                onClick = { 
                    WindowManagerHelper.launchAppInWindow(context, "com.google.android.calendar", "Calendar")
                }
            ),
            DockApp(
                packageName = "com.android.vending",
                label = "Play Store",
                icon = try {
                    packageManager.getApplicationIcon("com.android.vending")
                } catch (e: Exception) {
                    null
                },
                onClick = { 
                    WindowManagerHelper.launchAppInWindow(context, "com.android.vending", "Play Store")
                }
            ),
            DockApp(
                packageName = "com.whatsapp",
                label = "WhatsApp",
                icon = try {
                    packageManager.getApplicationIcon("com.whatsapp")
                } catch (e: Exception) {
                    null
                },
                onClick = { 
                    WindowManagerHelper.launchAppInWindow(context, "com.whatsapp", "WhatsApp")
                }
            ),
            DockApp(
                packageName = "com.google.android.youtube",
                label = "YouTube",
                icon = try {
                    packageManager.getApplicationIcon("com.google.android.youtube")
                } catch (e: Exception) {
                    null
                },
                onClick = { 
                    WindowManagerHelper.launchAppInWindow(context, "com.google.android.youtube", "YouTube")
                }
            ),
            DockApp(
                packageName = "com.google.android.gm",
                label = "Gmail",
                icon = try {
                    packageManager.getApplicationIcon("com.google.android.gm")
                } catch (e: Exception) {
                    null
                },
                onClick = { 
                    WindowManagerHelper.launchAppInWindow(context, "com.google.android.gm", "Gmail")
                }
            ),
            DockApp(
                packageName = "com.spotify.music",
                label = "Spotify",
                icon = try {
                    packageManager.getApplicationIcon("com.spotify.music")
                } catch (e: Exception) {
                    null
                },
                onClick = { 
                    WindowManagerHelper.launchAppInWindow(context, "com.spotify.music", "Spotify")
                }
            ),
            DockApp(
                packageName = "com.netflix.mediaclient",
                label = "Netflix",
                icon = try {
                    packageManager.getApplicationIcon("com.netflix.mediaclient")
                } catch (e: Exception) {
                    null
                },
                onClick = { 
                    WindowManagerHelper.launchAppInWindow(context, "com.netflix.mediaclient", "Netflix")
                }
            ),
        )
    }
    
    // Refresh running apps periodically
    var refreshTrigger by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5000) // Refresh every 5 seconds
            refreshTrigger++
        }
    }
    
    val updatedRunningApps = remember(refreshTrigger) {
        try {
            val runningAppPackages = RunningAppsManager.getRunningApps(context)
            Log.d("MacOSDock", "Got ${runningAppPackages.size} running app packages: ${runningAppPackages.joinToString()}")
            
            val dockApps = runningAppPackages.mapNotNull { packageName ->
                // Filter out our own app only
                if (packageName != "com.gpi.desktopmode") {
                    val dockApp = RunningAppsManager.createRunningAppDockApp(packageName, context)
                    Log.d("MacOSDock", "Created dock app for $packageName: ${dockApp != null}")
                    dockApp
                } else {
                    Log.d("MacOSDock", "Filtered out $packageName (our app)")
                    null
                }
            }
            
            Log.d("MacOSDock", "Final running apps for dock: ${dockApps.size}")
            dockApps
        } catch (e: Exception) {
            android.util.Log.e("MacOSDock", "Error getting running apps: ${e.message}")
            emptyList()
        }
    }
    
    val allDockApps = remember(pinnedApps, updatedRunningApps) {
        val apps = mutableListOf<DockApp>()
        apps.addAll(pinnedApps)
        if (updatedRunningApps.isNotEmpty()) {
            apps.add(DockApp("", "separator", null, { }, true))
            apps.addAll(updatedRunningApps)
        }
        Log.d("MacOSDock", "Total dock apps: ${apps.size} (pinned: ${pinnedApps.size}, running: ${updatedRunningApps.size})")
        apps
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