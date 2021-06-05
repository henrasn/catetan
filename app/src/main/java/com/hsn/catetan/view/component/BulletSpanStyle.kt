package com.hsn.catetan.view.component

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import com.hsn.catetan.common.utils.px

class BulletSpanStyle(val color: Int) : LeadingMarginSpan {
    override fun drawLeadingMargin(
        c: Canvas?,
        paint: Paint?,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence?,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        if ((text as Spanned).getSpanStart(this) == start && paint != null && c != null) {

            val style: Paint.Style = paint.style
            val oldColor = paint.color
            paint.color = color
            paint.style = Paint.Style.FILL
            val yPosition = (top + bottom) / 2f
            val xPosition: Float = x + dir * 12.px().toFloat()
            c.drawCircle(xPosition, yPosition, 12.px().toFloat(), paint)
            paint.color = oldColor
            paint.style = style
        }
    }

    override fun getLeadingMargin(first: Boolean): Int = 2 * 12.px() + 8.px()
}