package org.dallas.smartshelf.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

fun formatDate(dateTime: LocalDateTime): String {
    // Format as YYYY-MM-DD
    return "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')}"
}

fun formatDate(instant: kotlinx.datetime.Instant): String {
    val localDateTime = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    return "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
}