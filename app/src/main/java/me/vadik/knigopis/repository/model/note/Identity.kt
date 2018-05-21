package me.vadik.knigopis.repository.model.note

class Identity(
    val id: String,
    private val nickname: String?,
    private val booksCount: Int
) {
    val name get() = nickname ?: id
}