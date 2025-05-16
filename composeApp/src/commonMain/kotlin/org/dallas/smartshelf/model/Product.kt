package org.dallas.smartshelf.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.periodUntil
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Product(
    val id: String = generateUUID(),
    val barcode: String?,
    val name: String,
    val quantity: Int,
    val purchaseDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val expiryDate: LocalDateTime? = null,
    val category: ProductCategory,
    val lastModified: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val userId: String // For user association
) {
    fun isExpired(): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return expiryDate?.let { expiry ->
            now > expiry
        } ?: false
    }

    fun isExpiringSoon(): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return expiryDate?.let { expiry ->
            if (isExpired()) return false

            // Using periodUntil to calculate days between
            val period = now.date.periodUntil(expiry.date)
            val days = period.days + period.months * 30 + period.years * 365

            days <= 7
        } ?: false
    }

    fun matchesFilter(filter: ProductFilter): Boolean {
        // Check search query
        if (filter.searchQuery.isNotEmpty()) {
            val query = filter.searchQuery.lowercase()
            if (!name.lowercase().contains(query) && barcode?.contains(query, ignoreCase = true) == false) {
                return false
            }
        }

        // Check categories
        if (filter.categories.isNotEmpty() && !filter.categories.contains(category)) {
            return false
        }

        // Check expiry filters
        when {
            filter.showExpiredOnly && !isExpired() -> return false
            filter.showExpiringSoonOnly && !isExpiringSoon() -> return false
        }

        return true
    }

    companion object {
        // Simple UUID generator for multiplatform
        private fun generateUUID(): String {
            val randomBytes = ByteArray(16).apply {
                Random.nextBytes(this)
            }

            // Convert bytes to hex without using format
            val hexChars = "0123456789abcdef"
            val hexString = StringBuilder(36)

            for (i in randomBytes.indices) {
                val byte = randomBytes[i].toInt() and 0xFF
                hexString.append(hexChars[byte ushr 4])
                hexString.append(hexChars[byte and 0x0F])

                // Add hyphens for UUID format (8-4-4-4-12)
                if (i == 3 || i == 5 || i == 7 || i == 9) {
                    hexString.append('-')
                }
            }

            return hexString.toString()
        }
    }
}