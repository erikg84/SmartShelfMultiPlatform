package org.dallas.smartshelf.util

class IosPlatformContext : PlatformContext {
    override fun getApiBaseUrl(): String {
        // In a real app, you might load this from a config file
        return "http://localhost:8080/api"
    }

    override fun isDebugBuild(): Boolean {
        return true // You'd need a better way to determine this in a real app
    }

    override fun getPlatformName(): String = "iOS"
}