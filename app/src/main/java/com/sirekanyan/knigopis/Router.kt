package com.sirekanyan.knigopis

import android.net.Uri
import com.sirekanyan.knigopis.repository.model.Book

interface Router {
    fun openBookScreen(book: Book)
    fun openUserScreen(id: String, name: String, avatar: String?)
    fun openWebPage(uri: Uri)
}