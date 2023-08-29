package ch.heigvd.scanalyze

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class TextOverlay(graphicOverlay: GraphicOverlay) : GraphicOverlay.Graphic(graphicOverlay) {
    private val rectPaint: Paint = Paint().apply {
        color = RED
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }

    private var boundingBoxes: List<RectF> = listOf()

    companion object {
        private const val STROKE_WIDTH = 10.0f
        private const val RED = 0xFFFF0000.toInt()
    }

    fun updateBoundingBoxes(boxes: List<RectF>) {
        this.boundingBoxes = boxes
        postInvalidate()
    }

    override fun draw(canvas: Canvas) {
        for (box in boundingBoxes) {
            val rect = RectF(
                translateX(box.left),
                translateY(box.top),
                translateX(box.right),
                translateY(box.bottom)
            )
            canvas.drawRect(rect, rectPaint)
        }
    }
}
