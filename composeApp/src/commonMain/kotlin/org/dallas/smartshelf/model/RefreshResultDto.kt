package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshResultDto(
    val status: String,
    val message: String,
    val timestamp: String? = null,
    val duration: String? = null
)