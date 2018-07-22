package com.sirekanyan.knigopis.common.functions

import android.util.Log

private const val TAG = "Knigopis"

@Suppress("unused")
fun logWarn(message: String) = Log.w(TAG, message)

fun logError(message: String, throwable: Throwable?) = Log.e(TAG, message, throwable)
