package com.sirekanyan.knigopis.common.adapter

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.extensions.setSquareImage
import com.sirekanyan.knigopis.common.extensions.showNow
import kotlinx.android.synthetic.main.header.view.*
import kotlinx.android.synthetic.main.user_book.view.*

sealed class BookViewHolder(view: View) : RecyclerView.ViewHolder(view)

class BookHeaderViewHolder(private val view: View) : BookViewHolder(view) {

    private val context = view.context

    fun setTitle(title: String) {
        view.book_title.text = title
    }

    fun setTitle(@StringRes titleRes: Int) {
        view.book_title.text = context.getString(titleRes)
    }

    fun setBooksCount(count: Int) {
        view.books_count.text = context.resources.getQuantityString(
            R.plurals.common_header_books,
            count,
            count
        )
        view.books_count.showNow()
    }

    fun showTopDivider(visible: Boolean) {
        view.header_divider.showNow(visible)
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

    fun setBookImageUrl(url: String?) {
        view.bookImage.setSquareImage(url)
    }

}