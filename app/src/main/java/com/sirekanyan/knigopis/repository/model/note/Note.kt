package com.sirekanyan.knigopis.repository.model.note

import com.sirekanyan.knigopis.repository.model.Book
import java.util.*

class Note(
    override val id: String,
    override val title: String,
    override val author: String,
    val notes: String,
    private val createdAt: Date,
    val user: Identity
) : Book {
    // TODO https://trello.com/c/UymHYoPK
    val fixedCreatedAt
        get() = Date(createdAt.time + TimeZone.getDefault().rawOffset)
}