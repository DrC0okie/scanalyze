package ch.heigvd.scanalyze

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class TextOverlay : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val paint = Paint()
    private val rectList = mutableListOf<RectF>()

    init {
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.0f
    }

    fun updateRects(rectList: List<RectF>) {
        this.rectList.clear()
        this.rectList.addAll(rectList)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (rect in rectList) {
            canvas.drawRect(rect, paint)
        }
    }
}