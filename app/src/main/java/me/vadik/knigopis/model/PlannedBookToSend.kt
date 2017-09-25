package me.vadik.knigopis.model

class PlannedBookToSend(
    val title: String,
    val author: String,
    val priority: Int = 99,
    val notes: String = "// todo"
)