package me.vadik.knigopis.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import me.vadik.knigopis.adapters.BooksAdapter.BookViewHolder
import android.view.ViewGroup
import android.widget.TextView
import me.vadik.knigopis.R
import me.vadik.knigopis.inflate
import me.vadik.knigopis.model.Book

class BooksAdapter(private val books: List<Book>) : RecyclerView.Adapter<BookViewHolder>() {

  class BookViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    val title: TextView = rootView.findViewById(R.id.book_title)
    val author: TextView = rootView.findViewById(R.id.book_author)
    val date: TextView = rootView.findViewById(R.id.book_read_date)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      BookViewHolder(parent.inflate(R.layout.book))

  override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
    val book = books[position]
    holder.title.text = book.title
    holder.author.text = book.author
    holder.date.text = book.createdAt
  }

  override fun getItemCount() = books.size
}