package ch.heigvd.scanalyze.ocr

import ch.heigvd.scanalyze.receipt.JsonProduct
import ch.heigvd.scanalyze.receipt.JsonReceipt
import ch.heigvd.scanalyze.rule_sets.RulesetFactory
import com.google.mlkit.vision.text.Text
import java.lang.RuntimeException
import kotlin.collections.ArrayList
import kotlin.math.abs

fun Text.toReceipt(): JsonReceipt {
    try {
        return generateReceipt(resolveLines(getTextElements(this)))
    } catch (e: Exception) {
        throw RuntimeException("Could not generate receipt: $e")
    }
}

/**
 * Fills a list of textElements with the elements that are in each line of each block
 * @param text The ML Kit [Text] object
 * @return A list of [TextElement]
 */
private fun getTextElements(text: Text): List<TextElement> {
    val textElements = mutableListOf<TextElement>()
    for (textBlock in text.textBlocks) {
        for (line in textBlock.lines) {
            for (element in line.elements) {
                // Retrieve the element's rectangle bounding box
                val boundingBox = element.boundingBox

                // Create a TextElement object and add it to the list
                if (boundingBox != null) {
                    val textElement = TextElement(
                        text = element.text.lowercase(),
                        x = boundingBox.centerX(),
                        y = boundingBox.centerY(),
                        yRange = (boundingBox.top..boundingBox.bottom).reduceY(50),
                    )
                    textElements.add(textElement)
                }
            }
        }
    }
    return textElements
}

private fun resolveLines(textElements: List<TextElement>): List<List<TextElement>> {
    //Sort elements by their Y-coordinate
    val sortedByY = textElements.sortedBy { it.y }

    // Group elements into lines based on Y top and bottom reduced range
    val lines = mutableListOf<MutableList<TextElement>>()
    var currentLine = mutableListOf<TextElement>()

    for (element in sortedByY) {
        if (currentLine.isEmpty()) {
            currentLine.add(element)
        } else {
            // Determine whether the current element belongs to the current line
            val lastElementInLine = currentLine.last()
            if (isOverlap(lastElementInLine.yRange, element.yRange)) {
                currentLine.add(element)
            } else {
                // Sort the current line by X-coordinate before adding it to the lines list
                currentLine.sortBy { it.x }
                lines.add(currentLine)

                // Start a new line
                currentLine = mutableListOf(element)
            }
        }
    }
    if (currentLine.isNotEmpty()) {
        currentLine.sortBy { it.x }
        lines.add(currentLine)
    }
    return lines
}

private fun generateReceipt(lines: List<List<TextElement>>): JsonReceipt {

    val products: MutableList<JsonProduct> = ArrayList()
    var receiptDate = ""
    val ruleset = RulesetFactory.create(lines)

    lines@for (line in lines) {
        val productName = StringBuilder("")
        val prices: MutableList<Float> = ArrayList()
        var isEdible = false
        for (element in line) {
            val text = element.text
            if (ruleset.isMinLineSize(line.size)) {
                if (!ruleset.isPrice(text) && ruleset.isProduct(text)) {
                    // The text belongs to the product name
                    productName.append("$text ")
                } else if (prices.size == 0 && ruleset.isQuantity(text)) {
                    // The text is the quantity
                    prices.add(text.replace(",", ".").toFloat())
                } else if (ruleset.isPrice(text)) { // The text is a price
                    //The quantity has not been detected by the OCR engine, so we add it
                    if(prices.size == 0) prices.add(.0f)
                    prices.add(text.replace(",", ".").toFloat())
                } else if (prices.size > 1 && ruleset.isEdible(text)) {
                    isEdible = true
                }
            }
            if (ruleset.isDate(text)) {
                receiptDate = ruleset.getDate(text)
            }
        }
        if(isEdible){
            val quantity = ruleset.getQuantity(prices)
            val unitPrice = ruleset.getUnitPrice(prices)
            val discount = ruleset.getDiscount(prices)
            val totalPrice = ruleset.getTotalPrice(prices)
            products.add(JsonProduct(productName.toString(), quantity, unitPrice, discount, totalPrice))
        }
    }

    if (products.size == 0) {
        throw RuntimeException("No product found after parsing the receipt")
    }

    //Calculate the total
    val total = products.sumOf { it.totalPrice.toDouble() }.toFloat()

    return JsonReceipt("0", receiptDate, ruleset.getDateTimeNow(), ruleset.shop.shopName.lowercase(), "", products.toTypedArray(), total)
}

private fun isOverlap(r1: IntRange, r2: IntRange): Boolean {
    return r1.first <= r2.last && r1.last >= r2.first
}

private fun IntRange.reduceY(percent: Int): IntRange {
    val centerY = (first + last) / 2
    val halfHeight = abs(last - first) * (percent / 200.0)
    return (centerY - halfHeight).toInt()..(centerY + halfHeight).toInt()
}
