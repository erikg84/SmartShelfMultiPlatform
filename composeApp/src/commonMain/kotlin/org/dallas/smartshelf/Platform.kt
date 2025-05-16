package org.dallas.smartshelf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform