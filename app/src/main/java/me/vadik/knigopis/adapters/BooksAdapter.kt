package me.vadik.knigopis.adapters

import android.widget.TextView
import me.vadik.knigopis.R
import me.vadik.knigopis.model.Book

object BooksAdapter {
  fun create(books: List<Book>) = createAdapter<Book, TextView>(
      books,
      R.layout.book,
      Adapter(R.id.book_title) { text = it.title },
      Adapter(R.id.book_author) { text = it.author },
      Adapter(R.id.book_read_date) { text = it.createdAt }
  )
}