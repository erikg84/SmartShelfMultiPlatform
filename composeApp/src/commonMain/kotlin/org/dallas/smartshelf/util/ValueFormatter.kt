package org.dallas.smartshelf.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun Double.toCurrencyString(): String {
    val wholePart = this.toInt()
    val decimalPart = ((this.absoluteValue - wholePart.absoluteValue) * 100).toInt()
    return "$${wholePart}.${decimalPart.toString().padStart(2, '0')}"
}

fun Double.toWeightString(): String {
    val wholePart = this.toInt()
    val decimalPart = ((this.absoluteValue - wholePart.absoluteValue) * 1000).roundToInt()
    val adjustedWholePart = if (decimalPart == 1000) wholePart + 1 else wholePart
    val adjustedDecimalPart = if (decimalPart == 1000) 0 else decimalPart
    return "${adjustedWholePart}.${adjustedDecimalPart.toString().padStart(3, '0')}kg"
}

fun Double.toUnitPriceString(): String {
    val wholePart = this.toInt()
    val decimalPart = ((this.absoluteValue - wholePart.absoluteValue) * 100).roundToInt()
    val adjustedWholePart = if (decimalPart == 100) wholePart + 1 else wholePart
    val adjustedDecimalPart = if (decimalPart == 100) 0 else decimalPart
    return "@$${adjustedWholePart}.${adjustedDecimalPart.toString().padStart(2, '0')}/kg"
}