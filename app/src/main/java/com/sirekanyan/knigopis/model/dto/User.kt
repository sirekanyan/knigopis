package com.sirekanyan.knigopis.model.dto

class User(
    val id: String,
    private val nickname: String?,
    val profile: String?,
    private val identity: String?,
    val booksCount: Int
) {
    val name get() = nickname ?: id
    val profiles get() = listOfNotNull(profile, identity)
}