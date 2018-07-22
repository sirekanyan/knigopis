package com.sirekanyan.knigopis.model.dto

import java.util.*

class Note(
    val id: String,
    val title: String,
    val author: String,
    val notes: String,
    private val createdAt: Date,
    val user: Identity
) {
    // TODO https://trello.com/c/UymHYoPK
    val fixedCreatedAt
        get() = Date(createdAt.time + TimeZone.getDefault().rawOffset)
}