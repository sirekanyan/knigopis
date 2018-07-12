package com.sirekanyan.knigopis.repository.model.subscription

import com.sirekanyan.knigopis.repository.model.User

class Subscription(
    val subUser: User,
    private val lastBooksCount: Int
) {
    val newBooksCount get() = subUser.booksCount - lastBooksCount
}