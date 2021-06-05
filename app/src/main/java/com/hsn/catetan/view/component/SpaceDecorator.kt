package com.hsn.catetan.view.component

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SpaceDecorator(
    val space: Int,
    val orientation: Int,
    val start: Int = 0,
    val end: Int = 0,
    val side: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemCount = parent.adapter?.itemCount ?: 0
        if (parent.getChildAdapterPosition(view) == 0 && itemCount == 1) {
            setOutRect(outRect, start, start)
        } else if (parent.getChildAdapterPosition(view) == 0) {
            setOutRect(outRect, start, 0)
        } else if (parent.getChildAdapterPosition(view) == itemCount - 1) {
            setOutRect(outRect, space, end)
        } else {
            setOutRect(outRect, space, 0)
        }
    }

    private fun setOutRect(outRect: Rect, first: Int, second: Int) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            outRect.left = first
            outRect.right = second
            outRect.top = side
            outRect.bottom = side
        } else {
            outRect.top = first
            outRect.bottom = second
            outRect.left = side
            outRect.right = side
        }
    }

}