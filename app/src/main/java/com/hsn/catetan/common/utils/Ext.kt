package com.hsn.catetan.common.utils

import android.content.res.Resources
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

fun Int.px(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun EditText.onChange(value: (String) -> Unit) {
    addTextChangedListener {
        value(it.toString())
    }
}