package com.sirekanyan.knigopis.common.extensions

import android.net.Uri

private val HTTP_SCHEMES = setOf("http", "https")

fun String.orDefault(default: String) = if (isEmpty()) default else this

fun String.toUriOrNull() =
    Uri.parse(this).takeIf(Uri::isValidHttpLink)

private fun Uri.isValidHttpLink() =
    scheme in HTTP_SCHEMES && !host.isNullOrBlank()