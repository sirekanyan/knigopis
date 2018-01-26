package me.vadik.knigopis.model

class FinishedBook(
    override val id: String,
    override val title: String,
    override val author: String,
    val readDay: String,
    val readMonth: String,
    val readYear: String,
    val notes: String,
    val createdAt: String,
    val user: User
) : Book {
    val order
        get() = arrayOf(readYear, readMonth, readDay)
            .joinToString("") { it.padStart(4, '0') }
}