package com.sirekanyan.knigopis.feature.login

import android.net.Uri
import com.sirekanyan.knigopis.BuildConfig.*
import com.sirekanyan.knigopis.common.extensions.RANDOM_ID

val LOGIN_CALLBACK_URI: Uri =
    Uri.Builder()
        .scheme(LOGIN_CALLBACK_SCHEME)
        .authority(LOGIN_CALLBACK_HOST)
        .path(LOGIN_CALLBACK_PATH)
        .build()
private val LOGIN_URI: Uri =
    Uri.Builder()
        .scheme("https")
        .authority("ulogin.ru")
        .path("auth.php")
        .build()
private const val ULOGIN_PROVIDER_PARAM = "name"
private val ULOGIN_OTHER_PARAMS =
    mapOf(
        "app_name" to "SDK",
        "app_id" to "",
        "secret_key" to "",
        "mid" to "null_$RANDOM_ID",
        "lang" to "en",
        "fields" to "first_name,last_name",
        "optional" to "",
        "verify" to "",
        "window" to "3",
        "source" to "android",
        "host" to "ulogin.ru",
        "redirect_uri" to "",
        "callback" to "ucall",
        "screen" to "",
        "providers" to "",
        "q" to LOGIN_CALLBACK_URI.toString()
    )

fun buildLoginUri(provider: String): Uri =
    LOGIN_URI.buildUpon()
        .appendQueryParameter(ULOGIN_PROVIDER_PARAM, provider)
        .apply {
            ULOGIN_OTHER_PARAMS.forEach { (key, value) ->
                appendQueryParameter(key, value)
            }
        }
        .build()