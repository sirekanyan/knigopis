package com.sirekanyan.knigopis.common.extensions

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.annotation.StringRes
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.sirekanyan.knigopis.R

var isDarkTheme = false

val Context.systemClipboardManager: ClipboardManager
    get() = getAndroidSystemService(Context.CLIPBOARD_SERVICE)

val Context.systemInputMethodManager: InputMethodManager
    get() = getAndroidSystemService(Context.INPUT_METHOD_SERVICE)

val Context.systemConnectivityManager: ConnectivityManager
    get() = getAndroidSystemService(Context.CONNECTIVITY_SERVICE)

fun Context.startActivityOrNull(intent: Intent): Unit? =
    packageManager.resolveActivity(intent, 0)?.let {
        startActivity(intent)
    }

fun Context.toast(@StringRes messageId: Int, vararg args: Any) {
    Toast.makeText(this, getString(messageId, *args), Toast.LENGTH_SHORT).show()
}

fun Context.toast(@StringRes messageId: Int) {
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
}

fun Context.setDarkTheme(isDark: Boolean) {
    setTheme(if (isDark) R.style.DarkAppTheme else R.style.AppTheme)
    isDarkTheme = isDark
}

private inline fun <reified T> Context.getAndroidSystemService(name: String) =
    getSystemService(name) as T