package me.vadik.knigopis.adapters.books

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.user_book.view.*
import me.vadik.knigopis.hideNow
import me.vadik.knigopis.showNow

class BookViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    var title: String
        get() = view.bookTitle.text.toString()
        set(value) {
            view.bookTitle.text = value
        }

    var notes: String
        get() = view.bookNotes.text.toString()
        set(value) {
            if (value.isEmpty()) {
                view.bookNotes.hideNow()
            } else {
                view.bookNotes.showNow()
                view.bookNotes.text = value
            }
        }
}