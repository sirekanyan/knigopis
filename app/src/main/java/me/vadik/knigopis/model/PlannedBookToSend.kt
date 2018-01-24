package me.vadik.knigopis.model

class PlannedBookToSend(
    val title: String,
    val author: String,
    val notes: Notes,
    val priority: Int = 99 // todo: logic for setting priority
)