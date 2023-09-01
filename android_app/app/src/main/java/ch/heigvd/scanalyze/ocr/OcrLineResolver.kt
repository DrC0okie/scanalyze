package ch.heigvd.scanalyze.ocr

import ch.heigvd.scanalyze.Product
import ch.heigvd.scanalyze.Receipt
import com.google.mlkit.vision.text.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.math.abs

object OcrLineResolver {

    val pricePattern = """^\s?\d[\.,]\d{2}\s?$""".toRegex()
    val productNamePattern= """^ ?[\w${'$'}%&'()*+,.\/:;<=>?@[\]^_`{|}~ -]{2,10} ?${'$'}""".toRegex()
    val quantityPattern = """^\s?\d{1,2}\s?${'$'}""".toRegex()
    val weightPattern = """^(?=.*[.,])[0-9][0-9.,][0-9.,]\d{0,3}${'$'}""".toRegex()
    val productTypePattern = """^\s?1\s?${'$'}""".toRegex()
    val datePattern = """/^(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[13-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})${'$'}|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))${'$'}|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})${'$'}/gm""".toRegex()
    val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateIsoFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    var shopNames = listOf("migros", "coop", "aldi", "lidl")

    fun resolveLines(text: Text): List<String> {
        var productName = StringBuilder("")
        val receipt: Receipt
        val products: MutableList<Product> = ArrayList()
        val textElements = getTextElements(text)
        var reciptDate: LocalDate = LocalDate.now()
        var shopName: String

        // Step 1: Optionally filter out unwanted text elements
        // elements = filterElements(elements)

        //Sort elements by their Y-coordinate
        val sortedByY = textElements.sortedBy { it.y }

        // Step 3: Group elements into lines based on Y top and bottom reduced range
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
        // Don't forget to add the last line
        if (currentLine.isNotEmpty()) {
            currentLine.sortBy { it.x }
            lines.add(currentLine)
        }
        var prices: MutableList<Float> = ArrayList()

        for (line in lines){
            var isEdible = false
            var isShopFound = false
            if(line.size > 4){
                for(element in line){
                    val text = element.text
                    if(!pricePattern.matches(text) && productNamePattern.matches(text)){
                        productName.append(text)
                        continue
                    }
                    if(prices.size == 0 && (quantityPattern.matches(text)|| weightPattern.matches(text))){
                        prices.add(text.toFloat())
                        continue
                    }
                    if(prices.size > 0 && pricePattern.matches(text)){
                        prices.add(text.toFloat())
                        continue
                    }
                    if( prices.size > 0 && productTypePattern.matches(text)){
                        isEdible = true
                        continue
                    }
                    if(datePattern.matches(text)){
                        reciptDate = LocalDate.parse(text, dateFormat)
                    }
                    if(!isShopFound){
                        for(shop in shopNames){
                            if(text == shop){
                                isShopFound = true
                                shopName = shop
                                break
                            }
                        }
                    }
                }
            }
            if(isEdible){
                products.add(Product(0, productName.toString(),prices[0], prices[1], prices[2]))
            }
        }

        receipt = Receipt(0,
            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString(),
            reciptDate.format(dateIsoFormat).toString(),
            shopName,
            "",
            products,
            0.0)

        // Step 4: Reconstruct lines of text
        val reconstructedLines = lines.map { line ->
            line.joinToString(" ") { it.text}
        }

        return reconstructedLines
    }

    /**
     * Fills a list of textElements with the elements that are in each line of each block
     * @param text The ML Kit [Text] object
     * @return A list of [TextElement]
     */
    private fun getTextElements(text: Text): MutableList<TextElement>{
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

    private fun isOverlap(r1: IntRange, r2: IntRange): Boolean{
        return r1.first <= r2.last && r1.last >= r2.first
    }

    fun IntRange.reduceY(percent: Int): IntRange{
        val centerY = (first + last) / 2
        val halfHeight = abs(last - first) * (percent / 200.0)
        return (centerY - halfHeight).toInt()..(centerY + halfHeight).toInt()
    }

    private fun findShopName(text: List<String>, vararg queries: String): String?{
        for (query in queries){
            if (text.any { it.contains(query, ignoreCase = true) }){
                return query
            }
        }
        return null
    }
}

