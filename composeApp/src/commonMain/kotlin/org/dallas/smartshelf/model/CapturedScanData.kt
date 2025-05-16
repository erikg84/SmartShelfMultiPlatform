package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class CapturedScanData(
    val barcode: String? = null,
    val receipt: String? = null,
)