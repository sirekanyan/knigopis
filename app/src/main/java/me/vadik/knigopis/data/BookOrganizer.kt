package me.vadik.knigopis.data

import me.vadik.knigopis.Configuration
import me.vadik.knigopis.R
import me.vadik.knigopis.common.ResourceProvider
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.BookHeader
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.PlannedBook

interface BookOrganizer<T : Book> {

    fun sort(books: List<T>): List<T>

    fun group(books: List<T>): List<Pair<Book, BookHeader>>

    fun organize(books: List<T>): List<Pair<Book, BookHeader>> =
        group(sort(books))

}

class PlannedBookOrganizerImpl(
    private val resources: ResourceProvider,
    private val config: Configuration
) : BookOrganizer<PlannedBook> {

    override fun sort(books: List<PlannedBook>): List<PlannedBook> =
        if (config.sortingMode == 0) {
            books.sortedByDescending(PlannedBook::priority)
        } else {
            books.sortedByDescending(PlannedBook::updatedAt)
        }

    override fun group(books: List<PlannedBook>): List<Pair<Book, BookHeader>> {
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

}

class FinishedBookPrepareImpl(
    private val resources: ResourceProvider
) : BookOrganizer<FinishedBook> {

    override fun sort(books: List<FinishedBook>): List<FinishedBook> =
        books.sortedByDescending(FinishedBook::order)

    override fun group(books: List<FinishedBook>): List<Pair<Book, BookHeader>> {
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