package com.gpi.desktopmode.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.gpi.desktopmode.DockApp
import com.gpi.desktopmode.toBitmap

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
        targetValue = 1.0f + (waveEffect * 0.5f), // Back to 1.5x max scale to extend beyond dock
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )
    
    // Calculate offset to keep bottom alignment and allow icons to extend beyond dock
    val offsetY by animateDpAsState(
        targetValue = if (scale > 1.0f) {
            // Move up by half the extra height to keep bottom aligned
            (55.dp * (scale - 1.0f) / 2f) // Updated to use 55dp base size
        } else {
            0.dp
        },
        animationSpec = tween(durationMillis = 200),
        label = "offset"
    )
    
    // Update hover states
    LaunchedEffect(isHovered) {
        onHoverChanged(isHovered)
    }
    
    Box(
        modifier = Modifier
            .size(55.dp)
            .offset(y = -offsetY)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (app.icon == null) {
                    Modifier.background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.45f)
                            )
                        )
                    )
                } else {
                    Modifier
                }
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
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = app.label,
                modifier = Modifier
                    .size(55.dp)
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
        
        // Running indicator - small glowing dot at the bottom
        if (app.isRunning) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 8.dp)
                    .size(8.dp)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        CircleShape
                    )
            ) {
                // Add a subtle glow effect
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .offset(1.dp, 1.dp)
                )
            }
        }
    }
} 