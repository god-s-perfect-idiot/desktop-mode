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

@Composable
fun MacOSDesktopScreen() {
    var isAppDrawerVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
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
            isVisible = PermissionState.showUsageAccessPermissionDialog,
            onDismiss = { PermissionState.dismissUsageAccessPermissionDialog() },
            onRequestPermission = {
                PermissionState.dismissUsageAccessPermissionDialog()
                PermissionManager.requestUsageAccessPermission(context)
            }
        )
    }
} 