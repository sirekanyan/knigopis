package com.sirekanyan.knigopis.common.extensions

import android.net.Uri
import java.util.*
import kotlin.random.Random

val RANDOM_ID = Array(16) { Random.nextInt(0, 16).toString(16) }.joinToString("")

private val HTTP_SCHEMES = setOf("http", "https")

val String.lowercase get() = toLowerCase(Locale.getDefault())

fun String.orDefault(default: String) = if (isEmpty()) default else this

fun String.toUriOrNull() =
    Uri.parse(this).takeIf(Uri::isValidHttpLink)

private fun Uri.isValidHttpLink(): Boolean {
    val scheme: String? = scheme
    return scheme in HTTP_SCHEMES && !host.isNullOrBlank()
}