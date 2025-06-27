package com.gpi.desktopmode.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.gpi.desktopmode.PermissionState
import com.gpi.desktopmode.PermissionManager
import com.gpi.desktopmode.ui.UsageAccessRequestDialog
import com.gpi.desktopmode.WindowManagerHelper
import com.gpi.desktopmode.WindowContainer

@Composable
fun MacOSDesktopScreen() {
    var isAppDrawerVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val windowManager = remember { WindowManagerHelper.getWindowManager(context) }
    
    // Check permissions on launch
    LaunchedEffect(Unit) {
        PermissionState.checkAllPermissionsOnLaunch(context)
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Use the actual wallpaper as background (not blurred)
        WallpaperBackground()
        
        // macOS Top Bar at the top
        Box(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            MacOSTopBar()
        }
        
        // Window Container - this will show all open app windows
        WindowContainer(
            windowManager = windowManager,
            onWindowClose = { windowId ->
                windowManager.removeWindow(windowId)
            },
            onWindowMinimize = { windowId ->
                windowManager.minimizeWindow(windowId)
            },
            onWindowMaximize = { windowId ->
                windowManager.toggleMaximize(windowId)
            },
            onWindowDrag = { windowId, deltaX, deltaY ->
                windowManager.moveWindow(windowId, deltaX, deltaY)
            }
        )
        
        // macOS Dock at the bottom
        MacOSDock(
            onAppDrawerClick = { isAppDrawerVisible = true }
        )
        
        // App Drawer overlay
        AppDrawer(
            isVisible = isAppDrawerVisible,
            onDismiss = { isAppDrawerVisible = false }
        )
        
        // Overlay Permission Dialog
        PermissionRequestDialog(
            isVisible = PermissionState.showOverlayPermissionDialog,
            onDismiss = { PermissionState.dismissOverlayPermissionDialog() },
            onRequestPermission = {
                PermissionState.dismissOverlayPermissionDialog()
                PermissionManager.requestOverlayPermission(context)
                // After requesting, check usage access next
                if (!PermissionManager.hasUsageAccessPermission(context)) {
                    PermissionState.requestUsageAccessPermission()
                }
            }
        )
        // Usage Access Permission Dialog
        UsageAccessRequestDialog(
            isVisible = PermissionState.showUsageAccessDialog,
            onDismiss = { PermissionState.dismissUsageAccessDialog() },
            onRequestPermission = {
                PermissionState.dismissUsageAccessDialog()
                PermissionManager.requestUsageAccessPermission(context)
            }
        )
    }
} 