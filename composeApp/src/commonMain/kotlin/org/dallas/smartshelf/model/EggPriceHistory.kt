package org.dallas.smartshelf.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class EggPriceHistory(
    val prices: List<EggPrice>,
    val averagePrice: Double,
    val lowestPrice: Double,
    val highestPrice: Double,
    val priceChange30Days: Double,
    val lastUpdated: LocalDate
)