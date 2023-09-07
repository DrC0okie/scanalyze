package ch.heigvd.scanalyze.overlays

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CameraOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paintLines = Paint()
    private val paintTransparent = Paint()

    init {
        paintLines.color =
            ContextCompat.getColor(context, ch.heigvd.scanalyze.R.color.scanalyze_purple)
        paintLines.style = Paint.Style.STROKE
        paintLines.strokeWidth = 12f

        paintTransparent.color = Color.argb(127, 255, 255, 255)
        paintTransparent.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        // left crosshair coordinates
//        val leftCrosshairCenterX = width * 0.08f
//        val leftCrosshairCenterY = height * 0.05f // 10% of the height from the top
//
//        // right crosshair coordinates
//        val rightCrosshairCenterX = width * 0.92f
//        val rightCrosshairCenterY = height * 0.05f
//
//        // Trans-lucid rectangles
//        val leftRect = Rect(0, 0, leftCrosshairCenterX.toInt(), height)
//        val rightRect = Rect(rightCrosshairCenterX.toInt(), 0, width, height)
//        val topRect = Rect(leftCrosshairCenterX.toInt(), 0, rightCrosshairCenterX.toInt(), leftCrosshairCenterY.toInt())
//
//        // Draw the lines from top to bottom
//        canvas.drawRect(leftRect, paintTransparent)
//        canvas.drawRect(rightRect, paintTransparent)
//        canvas.drawRect(topRect, paintTransparent)
//
//        canvas.drawLine(
//            leftCrosshairCenterX,
//            leftCrosshairCenterY,
//            leftCrosshairCenterX + 100,
//            leftCrosshairCenterY,
//            paintLines
//        )
//        canvas.drawLine(
//            leftCrosshairCenterX,
//            leftCrosshairCenterY,
//            leftCrosshairCenterX,
//            leftCrosshairCenterY + 100,
//            paintLines
//        )
//
//        canvas.drawLine(
//            rightCrosshairCenterX - 100,
//            rightCrosshairCenterY,
//            rightCrosshairCenterX,
//            rightCrosshairCenterY,
//            paintLines
//        )
//
//        canvas.drawLine(
//            rightCrosshairCenterX,
//            rightCrosshairCenterY,
//            rightCrosshairCenterX,
//            rightCrosshairCenterY + 100,
//            paintLines
//        )
    }

}