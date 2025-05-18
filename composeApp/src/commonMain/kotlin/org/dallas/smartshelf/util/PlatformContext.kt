package org.dallas.smartshelf.util

interface PlatformContext {
    fun getApiBaseUrl(): String
    fun isDebugBuild(): Boolean
    fun getPlatformName(): String
}