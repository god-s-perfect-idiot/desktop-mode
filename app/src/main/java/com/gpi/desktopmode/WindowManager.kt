package com.gpi.desktopmode

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Maximize
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gpi.desktopmode.PermissionManager
import com.gpi.desktopmode.PermissionState

data class AppWindow(
    val id: String,
    val packageName: String,
    val appLabel: String,
    var x: Int = 100,
    var y: Int = 100,
    var width: Int = 800,
    var height: Int = 600,
    var isMaximized: Boolean = false,
    var isMinimized: Boolean = false,
    var zIndex: Int = 0
)

class AppWindowManager(private val context: Context) {
    private val windows = mutableListOf<AppWindow>()
    private var nextZIndex = 1
    
    fun createWindow(packageName: String, appLabel: String): AppWindow {
        val window = AppWindow(
            id = System.currentTimeMillis().toString(),
            packageName = packageName,
            appLabel = appLabel,
            zIndex = nextZIndex++
        )
        windows.add(window)
        return window
    }
    
    fun getWindows(): List<AppWindow> = windows.toList()
    
    fun removeWindow(windowId: String) {
        windows.removeAll { it.id == windowId }
    }
    
    fun bringToFront(windowId: String) {
        val window = windows.find { it.id == windowId }
        window?.let {
            it.zIndex = nextZIndex++
            windows.sortBy { w -> w.zIndex }
        }
    }
    
    fun moveWindow(windowId: String, deltaX: Int, deltaY: Int) {
        val window = windows.find { it.id == windowId }
        window?.let {
            it.x += deltaX
            it.y += deltaY
        }
    }
    
    fun resizeWindow(windowId: String, newWidth: Int, newHeight: Int) {
        val window = windows.find { it.id == windowId }
        window?.let {
            it.width = newWidth
            it.height = newHeight
        }
    }
    
    fun toggleMaximize(windowId: String) {
        val window = windows.find { it.id == windowId }
        window?.let {
            it.isMaximized = !it.isMaximized
            if (it.isMaximized) {
                // Store original size and position
                it.width = 1200
                it.height = 800
                it.x = 50
                it.y = 50
            }
        }
    }
    
    fun minimizeWindow(windowId: String) {
        val window = windows.find { it.id == windowId }
        window?.let {
            it.isMinimized = true
        }
    }
}

@Composable
fun WindowContainer(
    windowManager: AppWindowManager,
    onWindowClose: (String) -> Unit,
    onWindowMinimize: (String) -> Unit,
    onWindowMaximize: (String) -> Unit,
    onWindowDrag: (String, Int, Int) -> Unit
) {
    val windows = windowManager.getWindows()
    
    Box(modifier = Modifier.fillMaxSize()) {
        windows.forEach { window ->
            if (!window.isMinimized) {
                AppWindowView(
                    window = window,
                    onClose = { onWindowClose(window.id) },
                    onMinimize = { onWindowMinimize(window.id) },
                    onMaximize = { onWindowMaximize(window.id) },
                    onDrag = { deltaX, deltaY -> onWindowDrag(window.id, deltaX, deltaY) },
                    modifier = Modifier
                        .offset(
                            x = window.x.dp,
                            y = window.y.dp
                        )
                        .size(
                            width = window.width.dp,
                            height = window.height.dp
                        )
                )
            }
        }
    }
}

@Composable
fun AppWindowView(
    window: AppWindow,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onDrag: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.95f))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    onDrag(x.toInt(), y.toInt())
                }
            }
    ) {
        // Window title bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    Color.Black.copy(alpha = 0.8f),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App title
            Text(
                text = window.appLabel,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Window controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onMinimize,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Minimize,
                        contentDescription = "Minimize",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = onMaximize,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Maximize,
                        contentDescription = "Maximize",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        // App content area - this will contain the actual app
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
                .background(Color.White)
        ) {
            // Embedded app view will go here
            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        // This is where we'll embed the actual app activity
                        setBackgroundColor(android.graphics.Color.WHITE)
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { frameLayout ->
                // Embed the app activity here
                embedAppActivity(frameLayout, window.packageName)
            }
        }
    }
}

private fun embedAppActivity(container: FrameLayout, packageName: String) {
    try {
        val packageManager = container.context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        
        if (intent != null) {
            // Create a child activity container
            val activityContainer = FrameLayout(container.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            
            container.addView(activityContainer)
            
            // For now, we'll show a placeholder since embedding activities is complex
            // In a real implementation, you'd use ActivityGroup or similar approaches
            val placeholderView = ComposeView(container.context).apply {
                setContent {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "App: $packageName",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Black
                            )
                            Text(
                                text = "Window Mode Active",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "This window contains the app\n(Full embedding coming soon)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            activityContainer.addView(placeholderView)
        }
    } catch (e: Exception) {
        Log.e("WindowManager", "Error embedding app: ${e.message}")
    }
}

object WindowManagerHelper {
    private var windowManager: AppWindowManager? = null
    
    fun getWindowManager(context: Context): AppWindowManager {
        if (windowManager == null) {
            windowManager = AppWindowManager(context)
        }
        return windowManager!!
    }
    
    fun launchAppInWindow(context: Context, packageName: String, appLabel: String) {
        Log.d("WindowManagerHelper", "Launching $appLabel ($packageName) in window mode")
        
        try {
            val wm = getWindowManager(context)
            val window = wm.createWindow(packageName, appLabel)
            Log.d("WindowManagerHelper", "Window created: ${window.id}")
        } catch (e: Exception) {
            Log.e("WindowManagerHelper", "Error creating window: ${e.message}")
            e.printStackTrace()
        }
    }
} 