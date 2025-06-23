package com.gpi.desktopmode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpi.desktopmode.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MacOSTopBar() {
    var currentDateTime by remember { mutableStateOf("") }
    
    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            val dateTimeFormat = SimpleDateFormat("EEE dd MMM hh:mm a", Locale.getDefault())
            currentDateTime = dateTimeFormat.format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(
                Color.Black.copy(alpha = 0.4f)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Android icon and text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_android_logo),
                    contentDescription = "Android",
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
            
            // Right side - System icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TopBarIcon(
                    icon = Icons.Default.VolumeUp,
                    contentDescription = "Sound",
                    onClick = { /* TODO: Open sound settings */ }
                )
                
                TopBarIcon(
                    icon = Icons.Default.Keyboard,
                    contentDescription = "Keyboard Layout",
                    onClick = { /* TODO: Open keyboard settings */ }
                )
                
                TopBarIcon(
                    icon = Icons.Default.Bluetooth,
                    contentDescription = "Bluetooth",
                    onClick = { /* TODO: Open bluetooth settings */ }
                )
                
                TopBarIcon(
                    icon = Icons.Default.Wifi,
                    contentDescription = "WiFi",
                    onClick = { /* TODO: Open wifi settings */ }
                )
                
                TopBarIcon(
                    icon = Icons.Default.Search,
                    contentDescription = "Search",
                    onClick = { /* TODO: Open search */ }
                )
                
                TopBarIcon(
                    icon = Icons.Default.ToggleOn,
                    contentDescription = "Settings",
                    onClick = { /* TODO: Open settings */ }
                )
                
                TopBarIcon(
                    icon = Icons.Default.Psychology,
                    contentDescription = "Gemini",
                    onClick = { /* TODO: Open Gemini */ }
                )
                
                // Date and Time
                Text(
                    text = currentDateTime,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TopBarIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = Color.White
        )
    }
} 