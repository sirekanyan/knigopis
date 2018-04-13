package me.vadik.knigopis

import android.net.Uri
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.note.Identity
import me.vadik.knigopis.model.subscription.Subscription

interface Router {
    fun openEditBookScreen(book: Book)
    fun openUserScreen(user: Subscription)
    fun openUserScreen(user: Identity)
    fun openBrowser(uri: Uri)
    fun shareProfile(url: String)
}