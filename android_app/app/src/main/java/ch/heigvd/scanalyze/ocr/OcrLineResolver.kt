package ch.heigvd.scanalyze.ocr

import ch.heigvd.scanalyze.Product
import ch.heigvd.scanalyze.Receipt
import ch.heigvd.scanalyze.Shop
import com.google.mlkit.vision.text.Text
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.math.abs

val pricePattern = """^\s?\d[.,]\d{2}\s?$""".toRegex()
val productNamePattern = """^\s?\S{2,12}\s?$""".toRegex()
val quantityPattern = """^\s?\d{1,2}\s?$""".toRegex()
val weightPattern = """^(?=.*[.,])[0-9][0-9.,][0-9.,]\d{0,3}$""".toRegex()
val productTypePattern = """^\s?1\s?$""".toRegex()
val datePattern = """^\s?\d{2}\.\d{2}\.\d{4}\s?$""".toRegex()
val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
val dateIsoFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME

fun Text.toReceipt(): Receipt? {

    return generateReceipt(resolveLines(getTextElements(this)))

}

private fun resolveLines(textElements: MutableList<TextElement>): MutableList<MutableList<TextElement>> {
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

private fun generateReceipt(lines: MutableList<MutableList<TextElement>>): Receipt? {

    val products: MutableList<Product> = ArrayList()
    var receiptDate = LocalDate.now()
    var shopName = Shop.UNKNOWN
    var isShopFound = false

    for (line in lines) {
        var isEdible = false
        val productName = StringBuilder("")
        val prices: MutableList<Float> = ArrayList()


        for (element in line) {
            val text = element.text
            if (line.size > 4) {
                if (!pricePattern.matches(text) && productNamePattern.matches(text)) {
                    // The text belongs to the product name
                    productName.append("$text ")
                } else if (prices.size == 0 && (quantityPattern.matches(text) || weightPattern.matches(
                        text
                    ))
                ) {
                    // The text is the quantity
                    prices.add(text.toFloat())
                } else if (prices.size > 0 && pricePattern.matches(text)) {
                    // The text is a price
                    prices.add(text.toFloat())
                } else if (prices.size > 0 && productTypePattern.matches(text)) {
                    // The text is the code number indicating an edible product
                    isEdible = true
                }
            } else if (!isShopFound) {
                shopName = getShop(text)
                if (shopName != Shop.UNKNOWN) isShopFound = true
            }
            if (datePattern.matches(text)) {
                receiptDate = LocalDate.parse(text, dateFormat)
            }
        }
        if (isEdible) {
            products.add(Product(0, productName.toString(), prices[0], prices[1], prices[2]))
        }
    }

    if (products.size == 0) return null

    return Receipt(
        0,
        receiptDate.atStartOfDay().format(dateIsoFormat).toString(),
        LocalDateTime.now().format(dateIsoFormat).toString(),
        shopName,
        "",
        ArrayList(products)
    )
}

/**
 * Fills a list of textElements with the elements that are in each line of each block
 * @param text The ML Kit [Text] object
 * @return A list of [TextElement]
 */
private fun getTextElements(text: Text): MutableList<TextElement> {
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

private fun isOverlap(r1: IntRange, r2: IntRange): Boolean {
    return r1.first <= r2.last && r1.last >= r2.first
}

private fun IntRange.reduceY(percent: Int): IntRange {
    val centerY = (first + last) / 2
    val halfHeight = abs(last - first) * (percent / 200.0)
    return (centerY - halfHeight).toInt()..(centerY + halfHeight).toInt()
}

private fun getShop(name: String): Shop {
    return when (name.lowercase().filter { !it.isWhitespace() }) {
        Shop.MIGROS.name.lowercase() -> Shop.MIGROS
        Shop.COOP.name.lowercase() -> Shop.COOP
        Shop.ALDI.name.lowercase() -> Shop.ALDI
        Shop.LIDL.name.lowercase() -> Shop.LIDL
        else -> Shop.UNKNOWN
    }
}

