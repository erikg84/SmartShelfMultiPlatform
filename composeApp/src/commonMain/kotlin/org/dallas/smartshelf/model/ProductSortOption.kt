package org.dallas.smartshelf.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
enum class ProductSortOption {
    NAME,
    EXPIRY_DATE,
    PURCHASE_DATE,
    QUANTITY;

    fun comparator(): Comparator<Product> = when (this) {
        NAME -> compareBy { it.name.lowercase() }
        EXPIRY_DATE -> compareBy<Product> {
            it.expiryDate ?: MAX_DATE
        }
        PURCHASE_DATE -> compareByDescending { it.purchaseDate }
        QUANTITY -> compareByDescending { it.quantity }
    }

    companion object {
        // Create a very distant future date to use instead of MAX
        private val MAX_DATE = LocalDateTime(
            date = LocalDate(year = 9999, monthNumber = 12, dayOfMonth = 31),
            time = LocalTime(hour = 23, minute = 59, second = 59, nanosecond = 999_999_999)
        )
    }
}