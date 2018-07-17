package com.sirekanyan.knigopis.model

class NoteModel(
    val id: String,
    val bookTitle: String,
    val bookAuthor: String,
    val bookImage: String?,
    val noteContent: String,
    val noteDate: String,
    val userId: String,
    val userName: String,
    val userImage: String?
)