package com.sirekanyan.knigopis.common.functions

import com.sirekanyan.knigopis.BuildConfig

fun extra(name: String) = "${BuildConfig.APPLICATION_ID}.extra_$name"

fun createUserImageUrl(userId: String): String {
    return "${BuildConfig.STATIC_SERVER}/user/$userId"
}

fun createBookImageUrl(bookTitle: String): String {
    val normalizedTitle = bookTitle.toLowerCase()
        .replace(Regex("\\W+"), "_")
        .replace(Regex("(^_|_$)"), "")
        .replace("ё", "е")
    return "${BuildConfig.STATIC_SERVER}/book/$normalizedTitle"
}