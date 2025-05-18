package org.dallas.smartshelf.model

import kotlinx.datetime.LocalDate
import org.dallas.smartshelf.util.parseToLocalDateTime

fun EggNutritionResponse.toEggNutrition(): EggNutrition {
    return EggNutrition(
        eggType = eggType,
        description = description,
        fdcId = fdcId,
        servingSize = servingSize,
        servingSizeUnit = servingSizeUnit,
        calories = calories,
        protein = protein,
        totalFat = totalFat,
        cholesterol = cholesterol,
        nutrients = nutrients.mapValues { (_, nutrient) ->
            EggNutrition.Nutrient(
                name = nutrient.name,
                amount = nutrient.amount,
                unitName = nutrient.unitName
            )
        },
        lastUpdated = lastUpdated
    )
}

// EggPrice mappers
fun EggPriceResponse.EggPriceData.toEggPrice(): EggPrice {
    return EggPrice(
        eggType = eggType,
        price = price,
        date =parseToLocalDateTime(date),
        source = source
    )
}

fun EggPriceResponse.toEggPriceHistory(): EggPriceHistory {
    return EggPriceHistory(
        prices = prices.map { it.toEggPrice() },
        averagePrice = averagePrice,
        lowestPrice = lowestPrice,
        highestPrice = highestPrice,
        priceChange30Days = priceChange30Days,
        lastUpdated = parseToLocalDateTime(lastUpdated)
    )
}