package me.vadik.knigopis.adapters

import android.content.SharedPreferences
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.Single
import me.vadik.knigopis.ImageEndpoint
import me.vadik.knigopis.R
import me.vadik.knigopis.io2main
import me.vadik.knigopis.logError
import me.vadik.knigopis.model.Book
import java.util.concurrent.TimeUnit

class BooksAdapter(
    private val imageEndpoint: ImageEndpoint,
    private val preferences: SharedPreferences
) {

  fun create(books: List<Book>) = createAdapter<Book, View>(
      books,
      R.layout.book,
      Adapter(R.id.book_image) { book ->
        val cachedUrl = preferences.getString("book${book.id}", null)
        Single.defer {
          if (cachedUrl == null) {
            imageEndpoint.searchImage("${book.title} ${book.author}")
                .delay((Math.random() * 3000).toLong(), TimeUnit.MICROSECONDS)
                .map { thumbnail ->
                  ("https:" + thumbnail.url).also {
                    preferences.edit().putString("book${book.id}", it).apply()
                  }
                }
          } else {
            Single.just(cachedUrl)
          }
        }.io2main()
            .subscribe({ thumbnailUrl ->
              Glide.with(context)
                  .load(thumbnailUrl)
                  .apply(RequestOptions.circleCropTransform())
                  .into(this as ImageView)
            }, {
              logError("cannot load thumbnail", it)
            })
      },
      Adapter(R.id.book_title) {
        this as TextView
        text = it.title
      },
      Adapter(R.id.book_author) {
        this as TextView
        text = if (it.author.isEmpty()) {
          "(автор не указан)"
        } else {
          it.author
        }
      }
  )
}