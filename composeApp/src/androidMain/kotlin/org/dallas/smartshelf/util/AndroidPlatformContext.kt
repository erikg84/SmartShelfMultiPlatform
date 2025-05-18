package org.dallas.smartshelf.util

import android.content.Context

class AndroidPlatformContext(private val context: Context) : PlatformContext {
    override fun getApiBaseUrl(): String {
        // In a real app, you might load this from a config file or BuildConfig
        return "http://10.0.2.2:8080/api" // 10.0.2.2 is localhost for Android emulator
    }

    override fun isDebugBuild(): Boolean {
        return context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    override fun getPlatformName(): String = "Android"
}