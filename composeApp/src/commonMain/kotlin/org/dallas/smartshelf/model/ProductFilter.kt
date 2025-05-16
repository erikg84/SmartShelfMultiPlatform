package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductFilter(
    val searchQuery: String = "",
    val categories: Set<ProductCategory> = emptySet(),
    val showExpiredOnly: Boolean = false,
    val showExpiringSoonOnly: Boolean = false,
    val sortBy: ProductSortOption = ProductSortOption.NAME
)