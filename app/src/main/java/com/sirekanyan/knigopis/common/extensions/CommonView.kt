package com.sirekanyan.knigopis.common.extensions

import android.content.Context
import android.content.res.Resources
import com.google.android.material.snackbar.Snackbar
import com.sirekanyan.knigopis.common.android.toast.CommonView

val CommonView.context: Context
    get() = containerView.context

val CommonView.resources: Resources
    get() = containerView.resources

fun CommonView.toast(messageId: Int, vararg args: Any) {
    context.showToast(messageId, *args)
}

fun CommonView.toast(messageId: Int) {
    context.showToast(messageId)
}

fun CommonView.snackbar(messageId: Int) {
    Snackbar.make(containerView, messageId, Snackbar.LENGTH_LONG).show()
}