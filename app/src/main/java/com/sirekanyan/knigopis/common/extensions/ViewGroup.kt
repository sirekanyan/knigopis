package com.sirekanyan.knigopis.common.extensions

import android.support.annotation.LayoutRes
import android.support.v4.view.ViewGroupCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)

fun ViewGroup.setTransitionGroupCompat(isGroup: Boolean) {
    ViewGroupCompat.setTransitionGroup(this, isGroup)
}