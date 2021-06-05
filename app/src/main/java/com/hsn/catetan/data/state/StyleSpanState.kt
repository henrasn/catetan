package com.hsn.catetan.data.state

sealed class StyleSpanState {
    data class BoldState(var isBold: Boolean) : StyleSpanState()
    data class ItalicState(var isItalic: Boolean) : StyleSpanState()
    data class UnderlineState(var isUnderline: Boolean) : StyleSpanState()
}