package com.sirekanyan.knigopis.common.functions

import com.sirekanyan.knigopis.BuildConfig.APPLICATION_ID
import com.sirekanyan.knigopis.STATIC_API

fun extra(name: String) = "$APPLICATION_ID.extra_$name"

fun createUserImageUrl(userId: String): String {
    return "$STATIC_API/user/$userId"
}

fun createBookImageUrl(bookTitle: String): String {
    val normalizedTitle = bookTitle.toLowerCase()
        .replace(Regex("\\W+"), "_")
        .replace(Regex("(^_|_$)"), "")
        .replace("ё", "е")
    return "$STATIC_API/book/$normalizedTitle"
}