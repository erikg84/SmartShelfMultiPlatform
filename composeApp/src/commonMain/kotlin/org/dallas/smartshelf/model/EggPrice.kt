package org.dallas.smartshelf.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class EggPrice(
    val eggType: EggType,
    val price: Double,
    val date: LocalDateTime,
    val source: String
)