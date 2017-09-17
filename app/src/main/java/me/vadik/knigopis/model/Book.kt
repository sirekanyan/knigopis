package me.vadik.knigopis.model

class Book(
    val id: String,
    val title: String,
    val author: String,
    val notes: String,
    val createdAt: String,
    val user: User
)