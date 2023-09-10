package ch.heigvd.scanalyze.ocr

/**
 * Used store the text and its coordinates
 */
data class TextElement(val text: String, val x: Int, val y: Int, val yRange: IntRange)