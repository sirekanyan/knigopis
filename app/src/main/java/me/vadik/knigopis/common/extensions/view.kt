package me.vadik.knigopis.common.extensions

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

fun View.showNow(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}

fun View.showNow() {
    visibility = View.VISIBLE
}

fun View.hideNow() {
    visibility = View.GONE
}

fun View.snackbar(@StringRes messageId: Int) {
    Snackbar.make(this, messageId, Snackbar.LENGTH_LONG).show()
}