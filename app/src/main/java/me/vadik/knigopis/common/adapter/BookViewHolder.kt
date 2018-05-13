package me.vadik.knigopis.common.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.header.view.*
import kotlinx.android.synthetic.main.user_book.view.*
import me.vadik.knigopis.showNow

sealed class BookViewHolder(view: View) : RecyclerView.ViewHolder(view)

class BookHeaderViewHolder(private val view: View) : BookViewHolder(view) {

    fun setTitle(title: String) {
        view.book_title.text = title
    }

}

class BookItemViewHolder(private val view: View) : BookViewHolder(view) {

    fun setTitle(title: String) {
        view.bookTitle.text = title
    }

    fun setAuthor(author: String) {
        view.bookAuthor.text = author
    }

    fun setNotes(notes: String) {
        view.bookNotes.showNow(notes.isNotEmpty())
        view.bookNotes.text = notes
    }

    fun setOnLongClick(onLongClick: (View) -> Boolean) {
        view.setOnLongClickListener(onLongClick)
    }

}