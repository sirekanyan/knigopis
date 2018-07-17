package com.sirekanyan.knigopis.model.dto

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