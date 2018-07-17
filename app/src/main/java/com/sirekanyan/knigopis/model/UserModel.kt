package com.sirekanyan.knigopis.model

import android.text.SpannableString

class UserModel(
    val id: String,
    val name: String,
    val image: String?,
    val booksCount: String?,
    val newBooksCount: SpannableString?,
    val profiles: List<String>
)