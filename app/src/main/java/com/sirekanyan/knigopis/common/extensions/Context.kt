package com.sirekanyan.knigopis.common.extensions

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes

val Context.isNightMode: Boolean
    get() = resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES

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

fun Context.showToast(@StringRes messageId: Int, vararg args: Any) {
    Toast.makeText(this, getString(messageId, *args), Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes messageId: Int) {
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
}

private inline fun <reified T> Context.getAndroidSystemService(name: String) =
    getSystemService(name) as T