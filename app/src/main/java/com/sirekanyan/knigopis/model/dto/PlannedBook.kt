package com.sirekanyan.knigopis.model.dto

import java.util.*

class PlannedBook(
    val id: String,
    val userId: String,
    val createdAt: Date,
    val updatedAt: Date,
    override val title: String,
    val author: String,
    val priority: Int,
    val notes: String
) : Book