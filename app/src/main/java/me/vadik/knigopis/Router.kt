package me.vadik.knigopis

import android.net.Uri
import me.vadik.knigopis.repository.model.Book

interface Router {
    fun openBookScreen(book: Book)
    fun openUserScreen(id: String, name: String, avatar: String?)
    fun openWebPage(uri: Uri)
}