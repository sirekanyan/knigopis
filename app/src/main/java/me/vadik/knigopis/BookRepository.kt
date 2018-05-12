package me.vadik.knigopis

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.common.ResourceProvider
import me.vadik.knigopis.model.*

interface BookRepository {

    fun loadBooks(): Single<List<Book>>

    fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable

    fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable

}

class BookRepositoryImpl(
    private val api: Endpoint,
    private val auth: KAuth,
    private val resources: ResourceProvider
) : BookRepository {

    override fun loadBooks(): Single<List<Book>> =
        Singles.zip(
            api.getPlannedBooks(auth.getAccessToken())
                .map { it.sortedByDescending(PlannedBook::priority) },
            api.getFinishedBooks(auth.getAccessToken())
                .map { it.sortedByDescending(FinishedBook::order) }
                .map { it.groupFinishedBooks() }
        ).map { (planned, finished) ->
            mutableListOf<Book>().apply {
                if (planned.isNotEmpty()) {
                    add(BookHeader(resources.getString(R.string.books_header_todo)))
                }
                addAll(planned)
                addAll(finished)
            }
        }

    override fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable =
        when {
            bookId == null -> api.createFinishedBook(auth.getAccessToken(), book)
            done == null -> Completable.error(UnsupportedOperationException())
            done -> api.updateFinishedBook(bookId, auth.getAccessToken(), book)
            else -> {
                api.createFinishedBook(auth.getAccessToken(), book)
                    .andThen(api.deletePlannedBook(bookId, auth.getAccessToken()))
            }
        }

    override fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable =
        when {
            bookId == null -> api.createPlannedBook(auth.getAccessToken(), book)
            done == null -> Completable.error(UnsupportedOperationException())
            !done -> api.updatePlannedBook(bookId, auth.getAccessToken(), book)
            else -> {
                api.createPlannedBook(auth.getAccessToken(), book)
                    .andThen(api.deleteFinishedBook(bookId, auth.getAccessToken()))
            }
        }

    private fun List<FinishedBook>.groupFinishedBooks(): List<Book> {
        val groupedBooks = mutableListOf<Book>()
        var previousReadYear = Int.MAX_VALUE.toString()
        forEachIndexed { index, book ->
            val readYear = book.readYear
            if (previousReadYear != readYear) {
                groupedBooks.add(
                    BookHeader(
                        when {
                            book.readYear.isEmpty() ->
                                resources.getString(R.string.books_header_done_other)
                            index == 0 ->
                                resources.getString(R.string.books_header_done_first, readYear)
                            else ->
                                resources.getString(R.string.books_header_done, readYear)
                        }
                    )
                )
            }
            groupedBooks.add(book)
            previousReadYear = book.readYear
        }
        return groupedBooks
    }
}