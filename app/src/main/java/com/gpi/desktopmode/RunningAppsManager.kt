package com.gpi.desktopmode

// TODO: Create a permissions page to request Usage Access if not granted

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.util.*

object RunningAppsManager {
    
    fun checkUsageAccessPermission(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val calendar = Calendar.getInstance()
                val endTime = calendar.timeInMillis
                calendar.add(Calendar.MINUTE, -5) // Just check last 5 minutes
                val startTime = calendar.timeInMillis
                
                val usageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )
                
                Log.d("RunningApps", "Permission check: UsageStats returned ${usageStats.size} results")
                usageStats.isNotEmpty()
            } else {
                true // Assume permission granted on older versions
            }
        } catch (e: Exception) {
            Log.e("RunningApps", "Permission check failed: ${e.message}")
            false
        }
    }
    
    fun getRunningApps(context: Context): List<String> {
        val runningApps = mutableListOf<String>()
        
        // First check if we have permission
        if (!checkUsageAccessPermission(context)) {
            Log.w("RunningApps", "No usage access permission - running apps won't be available")
            return emptyList()
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                // Use UsageStatsManager for Android 5.1+ (API 22+)
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val calendar = Calendar.getInstance()
                val endTime = calendar.timeInMillis
                calendar.add(Calendar.HOUR, -1) // Get apps used in the last hour
                val startTime = calendar.timeInMillis
                
                val usageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )
                
                Log.d("RunningApps", "UsageStats query returned ${usageStats.size} results")
                
                // Get all recently used apps and filter at the end
                val sortedStats = usageStats.sortedByDescending { it.lastTimeUsed }
                runningApps.addAll(sortedStats.map { it.packageName }) // Get all apps
                
                Log.d("RunningApps", "Found ${runningApps.size} recently used apps: ${runningApps.joinToString()}")
            
                // Fallback for older Android versions (deprecated but still works)
                @Suppress("DEPRECATION")
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                @Suppress("DEPRECATION")
                val runningTasks = activityManager.getRunningTasks(10)
                runningApps.addAll(runningTasks.map { it.topActivity?.packageName ?: "" }.filter { it.isNotEmpty() })
                
                Log.d("RunningApps", "Found ${runningApps.size} running tasks: ${runningApps.joinToString()}")
            }
        } catch (e: Exception) {
            Log.e("RunningApps", "Error getting running apps: ${e.message}")
            e.printStackTrace()
        }
        
        // Filter to only show non-system apps
        val filteredApps = runningApps.filter { packageName ->
            try {
                val packageManager = context.packageManager
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                
                // Filter out our own app and system apps
                val isOurApp = packageName == "com.gpi.desktopmode"
                val isSystemApp = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                
                val shouldInclude = !isOurApp && !isSystemApp
                
                if (!shouldInclude) {
                    Log.d("RunningApps", "Filtered out: $packageName (our app: $isOurApp, system app: $isSystemApp)")
                } else {
                    Log.d("RunningApps", "Including: $packageName")
                }
                
                shouldInclude
            } catch (e: Exception) {
                Log.w("RunningApps", "Error filtering app $packageName: ${e.message}")
                false
            }
        }
        
        Log.d("RunningApps", "Final filtered list: ${filteredApps.size} apps: ${filteredApps.joinToString()}")
        return filteredApps.take(5) // Limit to 5 apps
    }
    
    fun createRunningAppDockApp(
        packageName: String,
        context: Context
    ): DockApp? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            
            Log.d("RunningApps", "Creating dock app for: $packageName")
            
            DockApp(
                packageName = packageName,
                label = appInfo.loadLabel(packageManager).toString(),
                icon = appInfo.loadIcon(packageManager),
                onClick = {
                    try {
                        val intent = packageManager.getLaunchIntentForPackage(packageName)
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    } catch (e: Exception) {
                        // Handle launch error silently
                    }
                },
                isRunning = true
            )
        } catch (e: Exception) {
            Log.e("RunningApps", "Error creating dock app for $packageName: ${e.message}")
            null
        }
    }
} 