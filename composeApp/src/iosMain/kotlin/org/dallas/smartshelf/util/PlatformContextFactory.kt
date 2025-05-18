package org.dallas.smartshelf.util

actual object PlatformContextFactory {
    actual fun create(): PlatformContext = IosPlatformContext()
}