package com.sirekanyan.knigopis.common.functions

import android.util.Log
import org.acra.ACRA

private const val TAG = "Knigopis"

@Suppress("unused")
fun logWarn(message: String) = Log.w(TAG, message)

fun logError(message: String, throwable: Throwable?): Int {
    ACRA.getErrorReporter().handleException(throwable)
    return Log.e(TAG, message, throwable)
}
