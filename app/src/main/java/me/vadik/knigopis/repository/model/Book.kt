package me.vadik.knigopis.repository.model

import me.vadik.knigopis.common.orDefault
import me.vadik.knigopis.repository.api.createBookImageUrl

interface Book {
    val id: String
    val title: String
    val author: String
    val bookImageUrl get() = createBookImageUrl(title)
    val titleOrDefault get() = title.orDefault("(без названия)")
    val authorOrDefault get() = author.orDefault("(автор не указан)")
    val fullTitle
        get() = when {
            author.isEmpty() -> titleOrDefault
            else -> "$titleOrDefault — $authorOrDefault"
        }
}