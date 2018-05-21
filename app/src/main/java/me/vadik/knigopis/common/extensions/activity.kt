package me.vadik.knigopis.common.extensions

import android.app.Activity

fun Activity.showKeyboard() {
    currentFocus?.let { view ->
        systemInputMethodManager.showSoftInput(view, 0)
    }
}

fun Activity.hideKeyboard() {
    currentFocus?.let { view ->
        systemInputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}