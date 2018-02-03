package me.vadik.knigopis.model.subscription

class Subscription(
    val subUser: SubUser,
    private val lastBooksCount: Int
) {
    val recentBooksCount get() = subUser.booksCount - lastBooksCount
}