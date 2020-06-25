package com.sirekanyan.knigopis.common.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.keepOnTop() {
    val linearLayoutManager = layoutManager as LinearLayoutManager
    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
        scrollToPosition(0)
    } else {
        stopScroll()
    }
}