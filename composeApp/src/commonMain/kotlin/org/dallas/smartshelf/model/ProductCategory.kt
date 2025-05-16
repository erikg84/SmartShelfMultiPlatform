package org.dallas.smartshelf.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProductCategory(val displayName: String) {
    DAIRY("Dairy"),
    PRODUCE("Produce"),
    MEAT("Meat & Seafood"),
    PANTRY("Pantry"),
    BEVERAGES("Beverages"),
    SNACKS("Snacks"),
    FROZEN("Frozen"),
    HOUSEHOLD("Household"),
    OTHER("Other");

    companion object {
        fun fromDisplayName(name: String): ProductCategory {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}