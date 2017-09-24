package me.vadik.knigopis.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import me.vadik.knigopis.R
import me.vadik.knigopis.api.BookCoverSearch
import me.vadik.knigopis.logError
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.BookHeader

class BooksAdapter(private val coverSearch: BookCoverSearch) {

  fun build(books: List<Book>) = Adapter(books) {
    if (it is BookHeader) {
      R.layout.header
    } else {
      R.layout.book
    }
  }
      .bind<ImageView>(R.id.book_image) {
        coverSearch.search(books[it])
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
        text = books[it].title
      }
      .bind<View>(R.id.header_divider) {
        visibility = if (it == 0) View.INVISIBLE else View.VISIBLE
      }
      .bind<TextView>(R.id.book_author) {
        text = if (books[it].author.isEmpty()) {
          "(автор не указан)"
        } else {
          books[it].author
        }
      }
      .build()
}