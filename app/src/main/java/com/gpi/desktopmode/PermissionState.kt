package com.gpi.desktopmode

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object PermissionState {
    var showOverlayPermissionDialog by mutableStateOf(false)
        private set
    var showUsageAccessPermissionDialog by mutableStateOf(false)
        private set
    
    fun requestOverlayPermission() {
        showOverlayPermissionDialog = true
    }
    
    fun dismissOverlayPermissionDialog() {
        showOverlayPermissionDialog = false
    }
    
    fun requestUsageAccessPermission() {
        showUsageAccessPermissionDialog = true
    }
    
    fun dismissUsageAccessPermissionDialog() {
        showUsageAccessPermissionDialog = false
    }
    
    fun checkAndRequestOverlayPermission(context: Context): Boolean {
        return if (PermissionManager.hasOverlayPermission(context)) {
            true
        } else {
            requestOverlayPermission()
            false
        }
    }
    
    fun checkAndRequestUsageAccessPermission(context: Context): Boolean {
        return if (PermissionManager.hasUsageAccessPermission(context)) {
            true
        } else {
            requestUsageAccessPermission()
            false
        }
    }
    
    fun checkAllPermissionsOnLaunch(context: Context) {
        val overlay = PermissionManager.hasOverlayPermission(context)
        val usage = PermissionManager.hasUsageAccessPermission(context)
        if (!overlay) showOverlayPermissionDialog = true
        else if (!usage) showUsageAccessPermissionDialog = true
    }
} 