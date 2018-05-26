package me.vadik.knigopis.repository.api

import io.reactivex.Single
import me.vadik.knigopis.common.io2main
import me.vadik.knigopis.repository.cache.BookCoverCache
import me.vadik.knigopis.repository.model.Book
import me.vadik.knigopis.repository.model.ImageThumbnail
import java.util.concurrent.TimeUnit

private const val MAX_DELAY_IN_MICROSECONDS = 3000
private const val MIN_TITLE_WORDS_COUNT = 2

interface BookCoverSearch {
    fun search(book: Book): Single<String>
    fun search(query: String): Single<List<String>>
}

class BookCoverSearchImpl(
    private val imageEndpoint: ImageEndpoint,
    private val bookCoverCache: BookCoverCache
) : BookCoverSearch {

    override fun search(book: Book): Single<String> =
        bookCoverCache.find(book.id).switchIfEmpty(
            searchThumbnail(getSearchQuery(book))
                .map { it.first() }
                .map { thumbnailUrl ->
                    bookCoverCache.put(book.id, thumbnailUrl)
                    thumbnailUrl
                }
        ).io2main()

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
}