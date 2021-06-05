package com.hsn.catetan.common.utils

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText
import android.widget.TextView
import com.hsn.catetan.data.state.SpanState

object EditorUtil {
    fun getStyleTypeCode(style: Any): Int {
        return if (style is UnderlineSpan) 3
        else if (style is StyleSpan) {
            if (style.style == Typeface.BOLD) 1 else 2
        } else -1
    }

    fun getStyleType(style: Int): Any? = when (style) {
        1 -> StyleSpan(Typeface.BOLD)
        2 -> StyleSpan(Typeface.ITALIC)
        3 -> UnderlineSpan()
        else -> null
    }

    fun mappingSpans(data: List<SpanState>, edt: EditText) {
        val span = SpannableStringBuilder(edt.text)
        data.forEach { state ->
            getStyleType(state.spanType)
                ?.let { style ->
                    span.setSpan(style, state.selectionStart, state.selectionEnd, 0)
                }
        }
        edt.setText(span, TextView.BufferType.SPANNABLE)
    }
}