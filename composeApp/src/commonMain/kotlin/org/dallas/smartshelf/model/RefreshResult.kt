package org.dallas.smartshelf.model

data class RefreshResult(
    val status: String,
    val message: String,
    val timestamp: String? = null,
    val duration: String? = null
)