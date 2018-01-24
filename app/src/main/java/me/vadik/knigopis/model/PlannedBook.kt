package me.vadik.knigopis.model

class PlannedBook(
    override val id: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
    override val title: String,
    override val author: String,
    val priority: Int,
    val notes: Notes
) : Book