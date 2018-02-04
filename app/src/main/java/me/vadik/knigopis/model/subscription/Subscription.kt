package me.vadik.knigopis.model.subscription

class Subscription(
    val subUser: SubUser,
    private val lastBooksCount: Int
) {
    val newBooksCount get() = (subUser.booksCount - lastBooksCount).takeIf { it > 0 }
}