package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class EggPriceResponse(
    val prices: List<EggPriceData>,
    val averagePrice: Double,
    val lowestPrice: Double,
    val highestPrice: Double,
    val priceChange30Days: Double,
    val lastUpdated: String
) {
    @Serializable
    data class EggPriceData(
        val eggType: EggType,
        val price: Double,
        val date: String,
        val source: String
    )
}