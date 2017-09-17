package me.vadik.knigopis

import android.app.Activity
import android.util.Log

private const val TAG = "Knigopis"

fun Activity.app() = application as App

fun Activity.logw(message: String) = Log.w(TAG, message)

fun Activity.log(message: String, throwable: Throwable?) = Log.e(TAG, message, throwable)
