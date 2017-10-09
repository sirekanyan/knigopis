package me.vadik.knigopis.api

import android.content.SharedPreferences
import io.reactivex.Single
import me.vadik.knigopis.io2main
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.ImageThumbnail
import java.util.concurrent.TimeUnit

private const val PREFERENCE_PREFIX = "thumbnail_"
private const val MAX_DELAY_IN_MICROSECONDS = 3000
private const val MIN_TITLE_WORDS_COUNT = 2

interface BookCoverSearch {
  fun search(book: Book): Single<String>
  fun search(query: String): Single<List<String>>
}

class BookCoverSearchImpl(
    private val imageEndpoint: ImageEndpoint,
    private val preferences: SharedPreferences
) : BookCoverSearch {

  override fun search(book: Book): Single<String> =
      Single.defer {
        val cachedUrl = getFromCache(book.id)
        if (cachedUrl == null) {
          searchThumbnail(getSearchQuery(book))
              .map { it.first() }
              .map { thumbnailUrl ->
                saveToCache(book.id, thumbnailUrl)
                thumbnailUrl
              }
        } else {
          Single.just(cachedUrl)
        }
      }.io2main()

  override fun search(query: String) =
      searchThumbnail(query)
          .io2main()

  private fun searchThumbnail(query: String) =
      imageEndpoint.searchImage(query)
          .delay((Math.random() * MAX_DELAY_IN_MICROSECONDS).toLong(), TimeUnit.MICROSECONDS)
          .map(ImageThumbnail::urls)

  private fun getSearchQuery(book: Book) =
      book.title.split(" ").size.let { titleWordsCount ->
        if (titleWordsCount <= MIN_TITLE_WORDS_COUNT) {
          "${book.title} ${book.author}"
        } else {
          book.title
        }
      }

  private fun saveToCache(bookId: String, url: String) =
      preferences.edit().putString(PREFERENCE_PREFIX + bookId, url).apply()

  private fun getFromCache(bookId: String) =
      preferences.getString(PREFERENCE_PREFIX + bookId, null)
}