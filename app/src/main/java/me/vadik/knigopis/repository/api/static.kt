package me.vadik.knigopis.repository.api

import me.vadik.knigopis.BuildConfig.STATIC_SERVER

fun createUserImageUrl(userId: String): String {
    return "$STATIC_SERVER/user/$userId"
}

fun createBookImageUrl(bookTitle: String): String {
    val normalizedTitle = bookTitle.toLowerCase()
        .replace(Regex("\\W+"), "_")
        .replace(Regex("(^_|_$)"), "")
        .replace("ё", "е")
    return "$STATIC_SERVER/book/$normalizedTitle"
}
