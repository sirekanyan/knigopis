package com.sirekanyan.knigopis.common.extensions

import android.animation.ObjectAnimator
import android.widget.ProgressBar

fun ProgressBar.setProgressSmoothly(progress: Int) {
    ObjectAnimator.ofInt(this, "progress", progress).start()
}