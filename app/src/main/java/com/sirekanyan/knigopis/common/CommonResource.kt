package com.sirekanyan.knigopis.common

import android.view.View

interface CommonResource<in T : View> {
    fun setValueTo(view: T)
}