package com.sirekanyan.knigopis.common.functions

import com.sirekanyan.knigopis.BuildConfig.APPLICATION_ID
import com.sirekanyan.knigopis.MAIN_WWW
import com.sirekanyan.knigopis.STATIC_API
import com.sirekanyan.knigopis.common.extensions.lowercase

fun extra(name: String) = "$APPLICATION_ID.extra_$name"

fun createUserPublicUrl(userId: String) = "$MAIN_WWW/#/user/books?u=$userId"

fun createUserImageUrl(userId: String) = "$STATIC_API/user/$userId"

fun createBookImageUrl(bookTitle: String): String {
    val normalizedTitle = bookTitle.lowercase
        .replace(Regex("\\W+"), "_")
        .replace(Regex("(^_|_$)"), "")
        .replace("ё", "е")
    return "$STATIC_API/book/$normalizedTitle"
}