package me.vadik.knigopis.adapters

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import me.vadik.knigopis.BookCoverSearch
import me.vadik.knigopis.R
import me.vadik.knigopis.logError
import me.vadik.knigopis.model.Book

class BooksAdapter(private val coverSearch: BookCoverSearch) {

  fun build(books: List<Book>) = Adapter(books, R.layout.book)
      .bind<ImageView>(R.id.book_image) { book ->
        coverSearch.search(book)
            .subscribe({ coverUrl ->
              Glide.with(context)
                  .load(coverUrl)
                  .apply(RequestOptions.circleCropTransform())
                  .into(this)
            }, {
              logError("cannot load thumbnail", it)
            })
      }
      .bind<TextView>(R.id.book_title) {
        text = it.title
      }
      .bind<TextView>(R.id.book_author) {
        text = if (it.author.isEmpty()) {
          "(автор не указан)"
        } else {
          it.author
        }
      }
      .build()
}