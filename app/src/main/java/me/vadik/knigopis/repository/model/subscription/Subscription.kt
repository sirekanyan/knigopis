package me.vadik.knigopis.repository.model.subscription

import me.vadik.knigopis.repository.model.User

class Subscription(
    val subUser: User,
    private val lastBooksCount: Int
) {
    val newBooksCount get() = subUser.booksCount - lastBooksCount
}