package org.dallas.smartshelf.util

expect object PlatformContextFactory {
    fun create(): PlatformContext
}