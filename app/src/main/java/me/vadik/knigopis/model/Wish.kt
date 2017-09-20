package me.vadik.knigopis.model

class Wish(
    val id: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
    val title: String,
    val author: String,
    val priority: Int,
    val notes: String
)