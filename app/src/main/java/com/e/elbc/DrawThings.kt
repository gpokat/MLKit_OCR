package com.e.elbc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.text.Text

/** Overlay for recognized words bounds.  */
class DrawThings constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private var sXO: Float = 1.0F
    private var sYO: Float = 1.0F
    private var wBounds: List<Text.Element> = mutableListOf()

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context!!, android.R.color.holo_green_light)
        strokeWidth = 1f
    }

    fun setOverlayScale(imHeight: Float, imWidth: Float) {
          sXO = (this.width.toFloat()*this.scaleX) / imWidth
          sYO = (this.height.toFloat()*this.scaleY) / imHeight
}


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.scale(sXO,sYO)
        wBounds.forEach{canvas.drawRect(it.boundingBox!!,paint)}
    }

    fun drawWordsBounds(recognizedWords: List<Text.Element>) {
        this.wBounds = recognizedWords
        invalidate()
    }
}