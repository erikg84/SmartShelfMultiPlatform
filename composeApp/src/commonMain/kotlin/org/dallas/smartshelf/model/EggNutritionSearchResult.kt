package org.dallas.smartshelf.model

data class EggNutritionSearchResult(
    val items: List<EggNutrition>,
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)