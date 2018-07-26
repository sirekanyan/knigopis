package com.sirekanyan.knigopis.common.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import ru.ulogin.sdk.UloginAuthActivity

private const val TEXT_MIME_TYPE = "text/plain"
private const val PACKAGE_SCHEME = "package"

fun createTextShareIntent(text: String, title: String): Intent {
    val sharingIntent = Intent(Intent.ACTION_SEND)
        .setType(TEXT_MIME_TYPE)
        .putExtra(Intent.EXTRA_TEXT, text)
    return Intent.createChooser(sharingIntent, title)
}

fun Context.createAppSettingsIntent(): Intent =
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts(PACKAGE_SCHEME, packageName, null)
    )

fun Context.createLoginIntent() =
    Intent(this, UloginAuthActivity::class.java)