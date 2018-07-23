package com.sirekanyan.knigopis

import android.net.Uri
import com.sirekanyan.knigopis.model.BookDataModel

interface Router {
    fun openBookScreen(book: BookDataModel)
    fun openUserScreen(id: String, name: String, image: String?)
    fun openWebPage(uri: Uri)
}