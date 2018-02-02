package me.vadik.knigopis.model.note

import java.util.*

class Note(
    val id: String,
    val title: String,
    val author: String,
    val notes: String,
    val createdAt: Date,
    val user: Identity
)