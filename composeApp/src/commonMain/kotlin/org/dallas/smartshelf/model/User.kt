package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null
)