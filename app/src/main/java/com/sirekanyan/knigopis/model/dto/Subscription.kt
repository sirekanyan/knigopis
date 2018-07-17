package com.sirekanyan.knigopis.model.dto

class Subscription(
    val subUser: User,
    private val lastBooksCount: Int
) {
    val newBooksCount get() = subUser.booksCount - lastBooksCount
}