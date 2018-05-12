package me.vadik.knigopis

import android.net.Uri
import me.vadik.knigopis.model.Book

interface Router {
    fun openEditBookScreen(book: Book)
    fun openUserScreen(id: String, name: String, avatar: String?)
    fun openBrowser(uri: Uri)
}