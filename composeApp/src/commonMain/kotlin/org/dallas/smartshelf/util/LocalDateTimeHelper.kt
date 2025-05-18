package org.dallas.smartshelf.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * Extension function to format LocalDateTime to ISO date string (YYYY-MM-DD)
 */
fun LocalDateTime.formatToIsoDate(): String {
    return "${this.year}-${this.monthNumber.padZero()}-${this.dayOfMonth.padZero()}"
}

/**
 * Helper function to pad single digits with leading zero
 */
private fun Int.padZero(): String {
    return if (this < 10) "0$this" else "$this"
}

/**
 * Format LocalDateTime to a more readable date format (e.g., "Jan 15, 2023")
 */
fun LocalDateTime.formatToReadableDate(): String {
    val month = when (this.monthNumber) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> "???"
    }

    return "$month ${this.dayOfMonth}, ${this.year}"
}

fun parseToLocalDateTime(dateString: String): LocalDateTime {
    // Split the date string by "-"
    val parts = dateString.split("-")

    // Check if we have the expected format
    if (parts.size != 3) {
        throw IllegalArgumentException("Date string should be in format yyyy-MM-dd, but was $dateString")
    }

    try {
        // Parse year, month, and day
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()

        // Create a LocalDate object
        val localDate = LocalDate(year, month, day)

        // Convert to LocalDateTime at the start of the day (midnight)
        return localDate.atStartOfDayIn(TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("Failed to parse date: $dateString", e)
    }
}

fun parseToLocalDateTime(dateString: String, format: String): LocalDateTime {
    when (format) {
        "yyyy-MM-dd" -> return parseToLocalDateTime(dateString)
        "MM/dd/yyyy" -> {
            val parts = dateString.split("/")
            if (parts.size != 3) {
                throw IllegalArgumentException("Date string should be in format MM/dd/yyyy, but was $dateString")
            }
            try {
                val month = parts[0].toInt()
                val day = parts[1].toInt()
                val year = parts[2].toInt()

                val localDate = LocalDate(year, month, day)
                return localDate.atStartOfDayIn(TimeZone.currentSystemDefault())
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Failed to parse date: $dateString", e)
            }
        }
        // Add more formats as needed
        else -> throw IllegalArgumentException("Unsupported date format: $format")
    }
}