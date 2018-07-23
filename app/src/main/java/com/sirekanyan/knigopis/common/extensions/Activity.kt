package com.sirekanyan.knigopis.common.extensions

import android.app.Activity
import android.view.View

fun Activity.getRootView(): View =
    findViewById(android.R.id.content)

fun Activity.showKeyboard(view: View) {
    if (view.requestFocus()) {
        systemInputMethodManager.showSoftInput(view, 0)
    }
}

fun Activity.hideKeyboard() {
    currentFocus?.let { view ->
        systemInputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}