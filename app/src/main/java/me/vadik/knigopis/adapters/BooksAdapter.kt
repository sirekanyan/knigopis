package me.vadik.knigopis.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import me.vadik.knigopis.ImageEndpoint
import me.vadik.knigopis.R
import me.vadik.knigopis.io2main
import me.vadik.knigopis.logError
import me.vadik.knigopis.model.Book
import java.util.concurrent.TimeUnit

class BooksAdapter(private val imageEndpoint: ImageEndpoint) {

  fun create(books: List<Book>) = createAdapter<Book, View>(
      books,
      R.layout.book,
      Adapter(R.id.book_image) { book ->
        imageEndpoint.searchImage(book.title + " " + book.author)
            .delay((Math.random() * 3000).toLong(), TimeUnit.MICROSECONDS)
            .io2main()
            .subscribe({ thumbnail ->
              Glide.with(context)
                  .load("https:" + thumbnail.url)
                  .apply(RequestOptions.circleCropTransform())
                  .into(this as ImageView)
            }, {
              logError("cannot load thumbnail", it)
            })
      },
      Adapter(R.id.book_title) { this as TextView; text = it.title },
      Adapter(R.id.book_author) { this as TextView; text = it.author }
  )
}