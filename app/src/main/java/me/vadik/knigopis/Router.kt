package me.vadik.knigopis

import me.vadik.knigopis.model.Book

interface Router {
    fun openEditBookScreen(book: Book)
}