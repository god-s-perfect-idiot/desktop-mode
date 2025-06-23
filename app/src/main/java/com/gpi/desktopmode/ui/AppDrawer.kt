package com.gpi.desktopmode.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpi.desktopmode.DockApp
import com.gpi.desktopmode.toBitmap

@Composable
fun AppDrawer(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { it }
        ),
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() }
        ) {
            BlurredWallpaperBackground(overlayColor = Color.Black.copy(alpha = 0.55f))
            // App Drawer Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp, start = 48.dp, end = 48.dp, bottom = 32.dp)
                ) {
                    // Search Bar at the very top, centered
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier.width(320.dp)) {
                            SearchBar(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    // Apps Grid with pagination (to be implemented next)
                    AppsGrid(searchQuery = searchQuery)
                    Spacer(modifier = Modifier.height(24.dp))
                    // Page Indicators (to be implemented next)
                    PageIndicators()
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search apps...",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun AppsGrid(searchQuery: String) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val columnsPerPage = 7
    val rowsPerPage = 5
    val appsPerPage = columnsPerPage * rowsPerPage
    
    val apps by remember(searchQuery) {
        derivedStateOf {
            try {
                val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                val userApps = installedApps.filter { app ->
                    app.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM == 0 &&
                    app.packageName != "com.gpi.desktopmode"
                }
                val allApps = userApps.mapNotNull { app ->
                    try {
                        DockApp(
                            packageName = app.packageName,
                            label = app.loadLabel(packageManager).toString(),
                            icon = app.loadIcon(packageManager),
                            onClick = { 
                                val intent = packageManager.getLaunchIntentForPackage(app.packageName)
                                intent?.let {
                                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(it)
                                }
                            }
                        )
                    } catch (e: Exception) {
                        null
                    }
                }.sortedBy { it.label }
                if (searchQuery.isNotEmpty()) {
                    allApps.filter { app ->
                        app.label.contains(searchQuery, ignoreCase = true)
                    }
                } else {
                    allApps
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    var currentPage by remember { mutableStateOf(0) }
    val pageCount = (apps.size + appsPerPage - 1) / appsPerPage
    val pageApps = apps.drop(currentPage * appsPerPage).take(appsPerPage)
    
    // Navigation logic for page indicators
    LaunchedEffect(apps.size, searchQuery) {
        if (currentPage >= pageCount) currentPage = 0
    }
    
    val paddedPageApps = pageApps + List(appsPerPage - pageApps.size) { null }
    
    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnsPerPage),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.fillMaxWidth().height((rowsPerPage * 100).dp)
        ) {
            items(paddedPageApps) { app ->
                if (app != null) {
                    AppGridItem(app = app)
                } else {
                    Spacer(modifier = Modifier.size(72.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PageIndicators(
            pageCount = pageCount,
            currentPage = currentPage,
            onPageSelected = { currentPage = it }
        )
    }
}

@Composable
private fun AppGridItem(app: DockApp) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { app.onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(94.dp)
                .scale(if (isHovered) 1.1f else 1.0f),
            colors = CardDefaults.cardColors(
                containerColor = if (isHovered) 
                    Color.White.copy(alpha = 0.95f) 
                else 
                    Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isHovered) 10.dp else 6.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (app.icon != null) {
                    val bitmap = remember(app.icon) {
                        app.icon.toBitmap()
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = app.label,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = app.label,
                        modifier = Modifier.size(72.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = app.label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 80.dp)
        )
    }
}

@Composable
private fun PageIndicators(pageCount: Int = 1, currentPage: Int = 0, onPageSelected: (Int) -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (index == currentPage) 12.dp else 8.dp)
                    .background(
                        color = if (index == currentPage) Color.White else Color.Gray.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .clickable { onPageSelected(index) }
            )
        }
    }
} 