package me.vadik.knigopis

import android.app.Activity
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

private const val TAG = "Knigopis"

fun Activity.app() = application as App

fun <T : View> Activity.findView(@IdRes id: Int): T = findViewById(id)

fun logWarn(message: String) = Log.w(TAG, message)

fun logError(message: String, throwable: Throwable?) = Log.e(TAG, message, throwable)

fun ViewGroup.inflate(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)
