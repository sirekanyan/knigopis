package me.vadik.knigopis.utils

import android.content.ClipboardManager
import android.content.Context
import android.view.inputmethod.InputMethodManager

val Context.systemClipboardManager: ClipboardManager
    get() = getAndroidSystemService(Context.CLIPBOARD_SERVICE)

val Context.systemInputMethodManager: InputMethodManager
    get() = getAndroidSystemService(Context.INPUT_METHOD_SERVICE)

private inline fun <reified T> Context.getAndroidSystemService(name: String) =
    getSystemService(name) as T