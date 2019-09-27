package com.sirekanyan.knigopis.common.extensions

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup

inline fun <reified T> ViewGroup.inflate(@LayoutRes layout: Int) =
    LayoutInflater.from(context).inflate(layout, this, false) as T