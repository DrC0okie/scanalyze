package ch.heigvd.scanalyze.ocr

import ch.heigvd.scanalyze.Product
import ch.heigvd.scanalyze.Receipt
import com.google.mlkit.vision.text.Text
import kotlin.math.abs

object OcrLineResolver {
    fun resolveLines(text: Text): List<String> {
        val textElements = getTextElements(text)

        // Step 1: Optionally filter out unwanted text elements
        // elements = filterElements(elements)

        //Sort elements by their Y-coordinate
        val sortedByY = textElements.sortedBy { it.y }

        // Step 3: Group elements into lines based on Y-coordinate
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

        // Step 4: Reconstruct lines of text
        val reconstructedLines = lines.map { line ->
            line.joinToString(" ") { it.text.lowercase() }
        }

        reconstructedLines.forEach { println(it) }

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
                            text = element.text,
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
}

