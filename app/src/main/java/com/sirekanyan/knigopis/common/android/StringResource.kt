package com.sirekanyan.knigopis.common.android

import android.widget.TextView
import androidx.annotation.StringRes

interface StringResource {
    fun setValueTo(view: TextView)
}

class PlainStringResource(private val text: String) : StringResource {
    override fun setValueTo(view: TextView) {
        view.text = text
    }
}

class IdStringResource(@StringRes private val textRes: Int) : StringResource {
    override fun setValueTo(view: TextView) {
        view.setText(textRes)
    }
}