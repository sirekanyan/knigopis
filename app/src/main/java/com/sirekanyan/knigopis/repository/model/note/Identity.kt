package com.sirekanyan.knigopis.repository.model.note

import com.sirekanyan.knigopis.repository.api.createUserImageUrl

class Identity(
    val id: String,
    private val nickname: String?,
    private val booksCount: Int
) {
    val name get() = nickname ?: id
    val avatarUrl get() = createUserImageUrl(id)
}