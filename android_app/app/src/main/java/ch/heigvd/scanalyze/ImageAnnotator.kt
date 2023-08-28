package ch.heigvd.scanalyze

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.text.Text

class ImageAnnotator {

    fun annotateImage(bitmap: Bitmap, visionText: Text): Bitmap {
        val annotatedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(annotatedBitmap)
        val paint = Paint()

        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f

        for (block in visionText.textBlocks) {
            val blockRect = block.boundingBox
            if (blockRect != null) {
                canvas.drawRect(blockRect, paint)

                for (line in block.lines) {
                    val lineRect = line.boundingBox
                    if (lineRect != null) {
                        canvas.drawRect(lineRect, paint)
                    }
                }
            }
        }
        return annotatedBitmap
    }
}
