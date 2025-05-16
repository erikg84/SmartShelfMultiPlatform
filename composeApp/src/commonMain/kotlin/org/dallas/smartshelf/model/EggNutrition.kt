package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class EggNutrition(
    val eggType: EggType,
    val description: String,
    val fdcId: Long,
    val servingSize: Double,
    val servingSizeUnit: String,
    val calories: Double,
    val protein: Double,
    val totalFat: Double,
    val cholesterol: Double,
    val nutrients: Map<String, Nutrient>,
    val lastUpdated: String
) {
    @Serializable
    data class Nutrient(
        val name: String,
        val amount: Double,
        val unitName: String
    )
}