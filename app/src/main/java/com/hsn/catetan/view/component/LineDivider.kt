package com.hsn.catetan.view.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hsn.catetan.R

class LineDivider(private val context: Context, val orientation: Int, val margin: Int = 0) :
    RecyclerView.ItemDecoration() {

    companion object {
        const val VERTICAL = 1
        const val HORIZONTAL = 2
    }

    var isExcludeLastItem = true
    private var lineDrawable: Drawable? = null
    private var transparentLineDrawable: Drawable? = null
    var bottomMargin: Int = 0

    init {
        transparentLineDrawable =
            ContextCompat.getDrawable(
                context,
                R.drawable.line_divider_transparent
            )
        lineDrawable = ContextCompat.getDrawable(
            context,
            R.drawable.line_divider
        )
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + margin
        val right = parent.width - parent.paddingRight - margin

        val childCount = parent.childCount

        if (childCount > 1) {
            for (x in 0..childCount) {
                val child = parent.getChildAt(x)
                child?.let {
                    val layoutParams = child.layoutParams as RecyclerView.LayoutParams

                    val top =
                        child.bottom + if (bottomMargin > 0) bottomMargin else layoutParams.bottomMargin
                    val bottom = top.plus(lineDrawable?.intrinsicHeight ?: 0)

                    if (isExcludeLastItem.and(x == childCount - 1))
                        drawLine(transparentLineDrawable, left, top, right, bottom, c)
                    else
                        drawLine(lineDrawable, left, top, right, bottom, c)
                }
            }
        }
    }

    private fun drawLine(
        drawable: Drawable?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        c: Canvas
    ) {
        drawable?.setBounds(left, top, right, bottom)
        drawable?.draw(c)
    }
}
