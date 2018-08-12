package com.sirekanyan.knigopis.common.extensions

import android.app.Activity
import android.view.ViewGroup
import com.sirekanyan.knigopis.App

val Activity.app get() = application as App

fun Activity.getRootView(): ViewGroup =
    findViewById(android.R.id.content)
