package com.hsn.catetan.view.component

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.BulletSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hsn.catetan.common.utils.EditorUtil
import com.hsn.catetan.common.utils.px
import com.hsn.catetan.data.state.SpanState
import com.hsn.catetan.data.state.StyleSpanState

class TextEditable(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
    androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, R.attr.editTextStyle)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.editTextStyle)

    val state: MutableLiveData<StyleSpanState>? = MutableLiveData()
    var startSelBold = 0
    var startSelItalic = 0
    var startSelUnderline = 0

    var boldSpanTemp: StyleSpan? = StyleSpan(Typeface.BOLD)
    var italicSpanTemp: StyleSpan? = StyleSpan(Typeface.ITALIC)
    var underlineSpanTemp: UnderlineSpan? = UnderlineSpan()

    private var boldStatic = false
        set(value) {
            field = value
            startSelBold = selectionStart
            state?.value = StyleSpanState.BoldState(field)
        }
    private var italicStatic = false
        set(value) {
            field = value
            startSelItalic = selectionStart
            state?.value = StyleSpanState.ItalicState(field)
        }
    private var underlineStatic = false
        set(value) {
            field = value
            startSelUnderline = selectionStart
            state?.value = StyleSpanState.UnderlineState(field)
        }

    init {
        addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                s?.run {
                    changeBoldSpan()
                    changeItalicSpan()
                    changeUnderlineSpan()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s?.takeIf { it.isNotEmpty() and (count > after) }?.run {
                    if (s.last().equals("\n")) {
                        val spans =
                            text?.getSpans(selectionEnd - 1, selectionEnd, BulletSpan::class.java)
                                .orEmpty()
                        spans.forEach { text?.removeSpan(it) }
                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.takeIf { it.isNotEmpty() }?.run {
                    if (before > count) {
                        if (startSelBold > selectionEnd) startSelBold = selectionEnd
                        if (startSelItalic > selectionEnd) startSelItalic = selectionEnd
                        if (startSelUnderline > selectionEnd) startSelUnderline = selectionEnd
                    }

                    if (selectionStart > 0 && get(selectionStart - 1) == ' ') {
                        startSelBold = start
                        startSelItalic = start
                        startSelUnderline = start
                        boldSpanTemp = null
                        italicSpanTemp = null
                        underlineSpanTemp = null
                    }
                }
            }

        })
    }

    private fun changeBoldSpan() {
        text?.run {
            if (boldStatic) {
                boldSpanTemp?.let { removeSpan(it) }
                boldSpanTemp = StyleSpan(Typeface.BOLD)
                setSpan(
                    boldSpanTemp,
                    startSelItalic,
                    selectionEnd,
                    0
                )
            } else {
                boldSpanTemp = null
            }
        }
    }

    private fun changeItalicSpan() {
        text?.run {
            if (italicStatic) {
                italicSpanTemp?.let { removeSpan(it) }
                italicSpanTemp = StyleSpan(Typeface.ITALIC)
                setSpan(
                    italicSpanTemp,
                    startSelItalic,
                    selectionEnd,
                    0
                )
            } else {
                italicSpanTemp?.let { removeSpan(it) }
                italicSpanTemp = null
            }
        }
    }

    private fun changeUnderlineSpan() {
        text?.run {
            if (underlineStatic) {
                underlineSpanTemp?.let { removeSpan(it) }
                underlineSpanTemp = UnderlineSpan()
                setSpan(
                    underlineSpanTemp,
                    startSelUnderline,
                    selectionEnd,
                    0
                )
            } else {
                underlineSpanTemp = null
            }
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        realTimeCheckSpan()
    }

    private fun realTimeCheckSpan() {
        val spans = getSpans(selectionStart, selectionEnd)
        postStateToDefault()
        spans.forEach { span ->
            text?.run {
                when {
                    (span is UnderlineSpan && getSpanFlags(span) != Spannable.SPAN_COMPOSING) -> state?.value =
                        StyleSpanState.UnderlineState(
                            true
                        )

                    (span is StyleSpan && span.style == Typeface.ITALIC) -> state?.value =
                        StyleSpanState.ItalicState(true)

                    (span is StyleSpan && span.style == Typeface.BOLD) -> state?.value =
                        StyleSpanState.BoldState(true)
                }
            }
        }

    }

    private fun postStateToDefault() {
        state?.value = StyleSpanState.UnderlineState(false)
        state?.value = StyleSpanState.ItalicState(false)
        state?.value = StyleSpanState.BoldState(false)
    }

    fun getAllSpans() = text?.let { text ->
        val spans = getSpans(0, text.lastIndex)
        val styles = mutableListOf<SpanState>()
        spans.forEach { style ->
            if (style is StyleSpan || (style is UnderlineSpan && text.getSpanFlags(style) != Spannable.SPAN_COMPOSING)) {
                styles.add(
                    SpanState(
                        text.getSpanStart(style),
                        text.getSpanEnd(style),
                        EditorUtil.getStyleTypeCode(style)
                    )
                )
            }
        }

        Gson().toJson(styles)
    } ?: ""

    fun getSpans(start: Int, end: Int): Array<out Any> {
        return text?.getSpans(start, end, Any::class.java) ?: arrayOf()
    }

    fun getSpans(what: Class<*>): Array<out Any> {
        return text?.getSpans(selectionStart, selectionEnd, what) ?: arrayOf()
    }

    fun updateUnderline() {
        if (selectionStart == selectionEnd) underlineStatic = underlineStatic.not()
        else {
            text?.let { text ->
                var underlineState = false
                val underlineSpan = getSpans(UnderlineSpan::class.java)
                if (underlineSpan.isEmpty()) {
                    setUnderline()
                    underlineState = true
                } else {
                    var found = false
                    underlineSpan.forEach { span ->
                        if (text.getSpanFlags(span) == 0) {
                            text.removeSpan(span)
                            found = true
                        }
                    }

                    if (found.not()) {
                        setUnderline()
                        underlineState = true
                    }
                }
                state?.postValue(StyleSpanState.UnderlineState(underlineState))
            }
        }
    }

    fun updateBold() {
        if (selectionStart == selectionEnd) boldStatic = boldStatic.not()
        else {
            text?.let { text ->
                var boldState = false
                val boldSpan = getSpans(StyleSpan::class.java)
                if (boldSpan.isEmpty()) {
                    setBold()
                    boldState = true
                } else {
                    var found = false
                    boldSpan.forEach { span ->
                        span as StyleSpan
                        if (span.style == Typeface.BOLD) {
                            text.removeSpan(span)
                            found = true
                        }
                    }

                    if (found.not()) {
                        setBold()
                        boldState = true
                    }
                }
                state?.postValue(StyleSpanState.BoldState(boldState))
            }
        }
    }

    fun updateItalic() {
        if (selectionStart == selectionEnd) italicStatic = italicStatic.not()
        else {
            text?.let { text ->
                var italicState = false
                val italicSpan = getSpans(StyleSpan::class.java)
                if (italicSpan.isEmpty()) {
                    setItalic()
                    italicState = true
                } else {
                    var found = false
                    italicSpan.forEach { span ->
                        span as StyleSpan
                        if (span.style == Typeface.ITALIC) {
                            text.removeSpan(span)
                            found = true
                        }
                    }

                    if (found.not()) {
                        setItalic()
                        italicState = true
                    }
                }
                state?.postValue(StyleSpanState.ItalicState(italicState))
            }
        }
    }

    fun updateUnsortedList() {
        text?.setSpan(
            BulletSpan(3.px()),
            selectionStart,
            selectionEnd,
            0
        )
        text?.append(" ")
    }

    private fun setBold() {
        text?.setSpan(StyleSpan(Typeface.BOLD), selectionStart, selectionEnd, 0)
    }

    private fun setItalic() {
        text?.setSpan(StyleSpan(Typeface.ITALIC), selectionStart, selectionEnd, 0)
    }

    private fun setUnderline() {
        text?.setSpan(UnderlineSpan(), selectionStart, selectionEnd, 0)
    }
}

