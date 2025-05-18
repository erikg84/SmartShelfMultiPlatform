package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class EggNutritionSearchResultDto(
    val items: List<EggNutritionResponse>,
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)