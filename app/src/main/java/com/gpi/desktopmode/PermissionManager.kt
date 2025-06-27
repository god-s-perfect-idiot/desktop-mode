package com.gpi.desktopmode

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log

object PermissionManager {
    
    fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Assume granted on older versions
        }
    }
    
    fun requestOverlayPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Log.d("PermissionManager", "Requesting overlay permission")
            } catch (e: Exception) {
                Log.e("PermissionManager", "Error requesting overlay permission: ${e.message}")
                // Fallback to general settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }
    
    fun hasUsageAccessPermission(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
                val calendar = java.util.Calendar.getInstance()
                val endTime = calendar.timeInMillis
                calendar.add(java.util.Calendar.MINUTE, -5)
                val startTime = calendar.timeInMillis
                
                val usageStats = usageStatsManager.queryUsageStats(
                    android.app.usage.UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )
                
                usageStats.isNotEmpty()
            } else {
                true // Assume granted on older versions
            }
        } catch (e: Exception) {
            Log.e("PermissionManager", "Error checking usage access: ${e.message}")
            false
        }
    }
    
    fun requestUsageAccessPermission(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Log.d("PermissionManager", "Requesting usage access permission")
        } catch (e: Exception) {
            Log.e("PermissionManager", "Error requesting usage access: ${e.message}")
        }
    }
} 