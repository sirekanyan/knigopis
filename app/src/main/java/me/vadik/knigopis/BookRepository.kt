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
                .map { it.sortedByDescending(PlannedBook::priority) }
                .map { groupPlannedBooks(it) },
            api.getFinishedBooks(auth.getAccessToken())
                .map { it.sortedByDescending(FinishedBook::order) }
                .map { groupFinishedBooks(it) }
        ).map { (planned, finished) ->
            mutableListOf<Pair<Book, BookHeader>>().apply {
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

    private fun groupPlannedBooks(books: List<PlannedBook>): List<Pair<Book, BookHeader>> {
        val result = mutableListOf<Pair<Book, BookHeader>>()
        val doingBooks = books.filterNot { it.priority == 0 }
        if (doingBooks.isNotEmpty()) {
            val todoHeaderTitle = resources.getString(R.string.books_header_doing)
            val doingHeader = BookHeader(todoHeaderTitle, doingBooks.size)
            result.add(doingHeader to doingHeader)
            result.addAll(doingBooks.map { it to doingHeader })
        }
        val todoBooks = books.filter { it.priority == 0 }
        if (todoBooks.isNotEmpty()) {
            val todoHeaderTitle = resources.getString(R.string.books_header_todo)
            val todoHeader = BookHeader(todoHeaderTitle, todoBooks.size)
            result.add(todoHeader to todoHeader)
            result.addAll(todoBooks.map { it to todoHeader })
        }
        return result
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