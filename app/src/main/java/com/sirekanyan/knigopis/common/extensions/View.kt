package com.sirekanyan.knigopis.common.extensions

import android.view.View
import androidx.annotation.DimenRes
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

val View.isVisible get() = visibility == View.VISIBLE

fun View.showNow(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}

fun View.showNow() {
    visibility = View.VISIBLE
}

fun View.hideNow() {
    visibility = View.GONE
}

fun View.show(value: Boolean) {
    if (value) show() else hide()
}

fun View.show() {
    animate().alpha(1f).setDuration(200)
        .withStartAction { visibility = View.VISIBLE }
}

fun View.hide() {
    animate().alpha(0f).setDuration(200)
        .withEndAction { visibility = View.GONE }
}

fun View.setElevationRes(@DimenRes elevation: Int) {
    ViewCompat.setElevation(this, resources.getDimensionPixelSize(elevation).toFloat())
}

fun View.startExpandAnimation() {
    alpha = 0f
    scaleX = 0f
    scaleY = 0f
    animate().alpha(1f).setDuration(200)
        .setInterpolator(LinearOutSlowInInterpolator())
        .scaleX(1f).scaleY(1f)
}

fun View.startCollapseAnimation() {
    animate().alpha(0f).setDuration(200)
        .setInterpolator(FastOutLinearInInterpolator())
        .scaleX(0f).scaleY(0f)
}

fun View.showKeyboard(view: View) {
    if (view.requestFocus()) {
        context.systemInputMethodManager.showSoftInput(view, 0)
    }
}

fun View.hideKeyboard() {
    findFocus()?.let { view ->
        context.systemInputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
