package me.vadik.knigopis.repository.model.note

private const val STATIC_SERVER = "https://knigopis.tk/img"

class Identity(
    val id: String,
    private val nickname: String?,
    private val booksCount: Int
) {
    val name get() = nickname ?: id
    val avatarUrl get() = "$STATIC_SERVER/user/$id.jpg"
}