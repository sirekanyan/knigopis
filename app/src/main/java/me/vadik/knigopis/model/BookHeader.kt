package me.vadik.knigopis.model

class BookHeader(
    override val title: String,
    override val id: String = "",
    override val author: String = ""
) : Book