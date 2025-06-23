package com.gpi.desktopmode

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gpi.desktopmode.ui.MacOSDesktopScreen
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
        
        // Make the app fullscreen after content is set
        makeFullscreen()
    }
    
    private fun makeFullscreen() {
        // Use a post callback to ensure the window is ready
        window.decorView.post {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For Android 11 (API 30) and above
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                // For Android 10 and below
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
                    android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
            }
        }
    }
}