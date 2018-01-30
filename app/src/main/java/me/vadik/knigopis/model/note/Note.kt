package me.vadik.knigopis.model.note

class Note(
    val id: String,
    val title: String,
    val author: String,
    val notes: String,
    val user: Identity
)