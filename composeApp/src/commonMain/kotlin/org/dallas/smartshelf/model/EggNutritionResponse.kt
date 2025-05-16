package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class EggNutritionResponse(
    val eggType: EggType,
    val description: String,
    val fdcId: Long,
    val servingSize: Double,
    val servingSizeUnit: String,
    val calories: Double,
    val protein: Double,
    val totalFat: Double,
    val cholesterol: Double,
    val nutrients: Map<String, NutrientData>,
    val lastUpdated: String
) {
    @Serializable
    data class NutrientData(
        val name: String,
        val amount: Double,
        val unitName: String
    )
}