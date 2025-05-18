package org.dallas.smartshelf.util

import org.dallas.smartshelf.SmartShelfApplication

actual object PlatformContextFactory {
    actual fun create(): PlatformContext {
        val context = SmartShelfApplication.getAppContext()
        return AndroidPlatformContext(context)
    }
}