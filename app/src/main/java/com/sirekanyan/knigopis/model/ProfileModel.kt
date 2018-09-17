package com.sirekanyan.knigopis.model

data class ProfileModel(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val profileUrl: String,
    val shareUrl: String
)