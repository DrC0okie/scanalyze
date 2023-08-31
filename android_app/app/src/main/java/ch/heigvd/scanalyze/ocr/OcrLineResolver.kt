package ch.heigvd.scanalyze.ocr

import com.google.mlkit.vision.text.Text
import kotlin.math.abs

object OcrLineResolver {
    fun resolveLines(text: Text,deltaHeight: Int): List<String> {
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
                            x = boundingBox.exactCenterX(),
                            y = boundingBox.exactCenterY()
                        )
                        textElements.add(textElement)
                        //println(textElement)
                    }
                }
            }
        }

        // Step 1: Optionally filter out unwanted text elements
        // elements = filterElements(elements)

        // Step 2: Sort elements by their Y-coordinate
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
                val deltaY = abs(element.y - lastElementInLine.y)

                if (deltaY < deltaHeight) {
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
            line.joinToString(" ") { it.text }
        }

        reconstructedLines.forEach { println(it) }

        return reconstructedLines
    }
}

