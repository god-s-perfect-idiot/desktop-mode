package com.gpi.desktopmode.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MacOSDesktopScreen() {
    var isAppDrawerVisible by remember { mutableStateOf(false) }
    
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
    }
} 