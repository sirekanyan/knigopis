package com.sirekanyan.knigopis.model.dto

import java.util.*

class PlannedBook(
    val id: String,
    val updatedAt: Date,
    val title: String,
    val author: String,
    val priority: Int,
    val notes: String
)