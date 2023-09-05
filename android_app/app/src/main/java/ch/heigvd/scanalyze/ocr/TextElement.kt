package ch.heigvd.scanalyze.ocr

data class TextElement(val text: String, val x: Int, val y: Int, val yRange: IntRange)