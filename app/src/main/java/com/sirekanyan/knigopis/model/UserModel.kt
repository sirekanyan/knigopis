package com.sirekanyan.knigopis.model

import android.text.SpannableString
import android.text.style.SuperscriptSpan

class UserModel(
    val id: String,
    val name: String,
    val image: String?,
    val booksCount: Int?,
    val newBooksCount: Int?,
    val profiles: List<String>
) {

    val newBooksCountFormatted: SpannableString?
        get() = newBooksCount?.let { "+$it" }?.let { count ->
            SpannableString(count).also {
                it.setSpan(SuperscriptSpan(), 0, count.length, 0)
            }
        }

}