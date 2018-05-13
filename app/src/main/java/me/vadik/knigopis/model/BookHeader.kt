package me.vadik.knigopis.model

class BookHeader(
    override val title: String,
    val count: Int,
    override val id: String = "",
    override val author: String = ""
) : Book