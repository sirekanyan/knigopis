package me.vadik.knigopis.adapters.books

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.vadik.knigopis.R
import me.vadik.knigopis.inflate

class BooksAdapter(
    private val books: List<UserBook>
) : RecyclerView.Adapter<BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookViewHolder(parent.inflate(R.layout.user_book))

    override fun getItemCount() =
        books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.title = "\"${book.title}\" — ${book.author}"
        holder.notes = book.notes
    }
}