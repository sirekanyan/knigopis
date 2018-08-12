package com.sirekanyan.knigopis.common.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import com.sirekanyan.knigopis.R
import ru.ulogin.sdk.UloginAuthActivity

private const val TEXT_MIME_TYPE = "text/plain"
private const val PACKAGE_SCHEME = "package"

fun Context.createProfileShareIntent(text: String) =
    createTextShareIntent(text, getString(R.string.profile_title_share))

fun Context.createAppSettingsIntent() =
    Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts(PACKAGE_SCHEME, packageName, null))

fun Context.createLoginIntent() =
    Intent(this, UloginAuthActivity::class.java)

private fun createTextShareIntent(text: String, title: String): Intent =
    Intent(Intent.ACTION_SEND)
        .setType(TEXT_MIME_TYPE)
        .putExtra(Intent.EXTRA_TEXT, text)
        .let { Intent.createChooser(it, title) }