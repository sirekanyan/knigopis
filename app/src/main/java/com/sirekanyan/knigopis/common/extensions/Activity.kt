package com.sirekanyan.knigopis.common.extensions

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.sirekanyan.knigopis.App

fun Activity.app() = application as App

fun Activity.getRootView(): ViewGroup =
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