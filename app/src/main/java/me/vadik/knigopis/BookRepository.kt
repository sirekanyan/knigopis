package me.vadik.knigopis

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.common.ResourceProvider
import me.vadik.knigopis.model.*

interface BookRepository {

    fun loadBooks(): Single<List<Pair<Book, BookHeader>>>

    fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable

    fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable

}

class BookRepositoryImpl(
    private val api: Endpoint,
    private val auth: KAuth,
    private val resources: ResourceProvider
) : BookRepository {

    override fun loadBooks(): Single<List<Pair<Book, BookHeader>>> =
        Singles.zip(
            api.getPlannedBooks(auth.getAccessToken())
                .map { it.sortedByDescending(PlannedBook::priority) },
            api.getFinishedBooks(auth.getAccessToken())
                .map { it.sortedByDescending(FinishedBook::order) }
                .map { groupFinishedBooks(it) }
        ).map { (planned, finished) ->
            mutableListOf<Pair<Book, BookHeader>>().apply {
                if (planned.isNotEmpty()) {
                    val todoHeaderTitle = resources.getString(R.string.books_header_todo)
                    val todoHeader = BookHeader(todoHeaderTitle, planned.size)
                    add(todoHeader to todoHeader)
                    addAll(planned.map { it to todoHeader })
                }
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

    private fun groupFinishedBooks(books: List<FinishedBook>): List<Pair<Book, BookHeader>> {
        var first = true
        return books.groupBy { it.readYear }
            .toSortedMap(Comparator { year1, year2 ->
                year2.compareTo(year1)
            })
            .flatMap { (year, books) ->
                val headerTitle = when {
                    year.isEmpty() -> resources.getString(R.string.books_header_done_other)
                    first -> {
                        first = false
                        resources.getString(R.string.books_header_done_first, year)
                    }
                    else -> resources.getString(R.string.books_header_done, year)
                }
                val header = BookHeader(headerTitle, books.size)
                val items = books.map { it to header }
                listOf(header to header, *items.toTypedArray())
            }
    }

}