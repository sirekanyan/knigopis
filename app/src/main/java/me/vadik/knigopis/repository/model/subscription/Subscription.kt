package me.vadik.knigopis.repository.model.subscription

class Subscription(
    val subUser: SubUser,
    private val lastBooksCount: Int
) {
    val newBooksCount get() = subUser.booksCount - lastBooksCount
}