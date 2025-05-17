package org.dallas.smartshelf.util

data class ReceiptItem(
    val name: String,
    val quantity: Double? = null,
    val unitPrice: Double? = null,
    val totalPrice: Double? = null
)

class ReceiptParser {

    fun parseReceipt(text: String): List<ReceiptItem> {
        val lines = text.split("\n")
        val items = mutableListOf<ReceiptItem>()
        var currentItem = mutableMapOf<String, Any?>()

        lines.forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isBlank() || isMetadataLine(trimmedLine)) {
                if (currentItem.isNotEmpty()) {
                    createAndAddItem(currentItem, items)
                    currentItem = mutableMapOf()
                }
                return@forEach
            }

            when {
                // Pattern 1: Price followed by item name (e.g., "$4.66 ZUCCHINI GREEN")
                trimmedLine.matches(Regex("\\$\\d+\\.\\d+\\s+.*")) -> {
                    if (currentItem.isNotEmpty()) {
                        createAndAddItem(currentItem, items)
                        currentItem = mutableMapOf()
                    }

                    val (price, name) = extractPriceAndName(trimmedLine)
                    currentItem["totalPrice"] = price
                    currentItem["name"] = name
                }

                // Pattern 2: Weight and unit price (e.g., "0.778kg NET @ $5.99/kg")
                isWeightAndPriceLine(trimmedLine) -> {
                    val weight = extractWeight(trimmedLine)
                    val unitPrice = extractUnitPrice(trimmedLine)

                    currentItem["quantity"] = weight
                    currentItem["unitPrice"] = unitPrice

                    // If we have a complete item, add it
                    if (currentItem["name"] != null) {
                        createAndAddItem(currentItem, items)
                        currentItem = mutableMapOf()
                    }
                }

                // Pattern 3: Just an item name
                else -> {
                    // Skip special lines
                    if (!isSpecialLine(trimmedLine)) {
                        if (currentItem.isEmpty()) {
                            currentItem["name"] = trimmedLine
                        } else {
                            // If we already have an item in progress, complete it first
                            createAndAddItem(currentItem, items)
                            currentItem = mutableMapOf("name" to trimmedLine)
                        }
                    }
                }
            }
        }

        // Handle any remaining item
        if (currentItem.isNotEmpty()) {
            createAndAddItem(currentItem, items)
        }

        return items
    }

    private fun isMetadataLine(line: String): Boolean {
        val metadataKeywords = listOf(
            "DATE", "SUBTOTAL", "TOTAL", "CASH", "CHANGE", "LOYALTY",
            "WED", "THU", "FRI", "SAT", "SUN", "MON", "TUE"
        )
        return metadataKeywords.any { keyword ->
            line.contains(keyword, ignoreCase = true)
        }
    }

    private fun isSpecialLine(line: String): Boolean {
        return line.equals("SPECIAL", ignoreCase = true)
    }

    private fun isWeightAndPriceLine(line: String): Boolean {
        return line.contains("kg") && (line.contains("/kg") || line.contains("NET"))
    }

    private fun extractPriceAndName(line: String): Pair<Double?, String> {
        val pricePattern = "\\$(\\d+\\.\\d+)".toRegex()
        val price = pricePattern.find(line)?.groupValues?.get(1)?.toDoubleOrNull()
        val name = line.replace(pricePattern, "").trim()
        return Pair(price, name)
    }

    private fun extractWeight(line: String): Double? {
        val weightPattern = "(\\d+\\.\\d+)\\s*kg".toRegex()
        return weightPattern.find(line)?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun extractUnitPrice(line: String): Double? {
        val patterns = listOf(
            "\\$(\\d+\\.\\d+)/kg".toRegex(),  // Standard format
            "@\\s*\\$(\\d+\\.\\d+)".toRegex()  // Alternative format
        )

        patterns.forEach { pattern ->
            pattern.find(line)?.groupValues?.get(1)?.toDoubleOrNull()?.let { return it }
        }
        return null
    }

    private fun createAndAddItem(
        itemData: MutableMap<String, Any?>,
        items: MutableList<ReceiptItem>
    ) {
        val name = (itemData["name"] as? String)?.trim() ?: return
        if (name.isBlank()) return

        // Create item only if we have at least a name and some price information
        val quantity = itemData["quantity"] as? Double
        val unitPrice = itemData["unitPrice"] as? Double
        val totalPrice = itemData["totalPrice"] as? Double

        // Calculate total price if missing but we have quantity and unit price
        val calculatedTotalPrice = when {
            totalPrice != null -> totalPrice
            quantity != null && unitPrice != null -> quantity * unitPrice
            else -> null
        }

        if (calculatedTotalPrice != null || (quantity != null && unitPrice != null)) {
            items.add(ReceiptItem(
                name = name,
                quantity = quantity,
                unitPrice = unitPrice,
                totalPrice = calculatedTotalPrice
            ))
        }
    }
}