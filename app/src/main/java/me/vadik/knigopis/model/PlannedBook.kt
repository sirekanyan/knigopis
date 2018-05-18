package me.vadik.knigopis.model

import java.util.*

class PlannedBook(
    override val id: String,
    val userId: String,
    val createdAt: Date,
    val updatedAt: Date,
    override val title: String,
    override val author: String,
    val priority: Int,
    val notes: String
) : Book