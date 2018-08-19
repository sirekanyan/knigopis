package com.sirekanyan.knigopis.common.extensions

import android.support.design.widget.Snackbar
import com.sirekanyan.knigopis.common.android.toast.CommonView

fun CommonView.toast(messageId: Int, vararg args: Any) {
    containerView.context.showToast(messageId, *args)
}

fun CommonView.toast(messageId: Int) {
    containerView.context.showToast(messageId)
}

fun CommonView.snackbar(messageId: Int) {
    Snackbar.make(containerView, messageId, Snackbar.LENGTH_LONG).show()
}