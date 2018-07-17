package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.ResourceProvider
import com.sirekanyan.knigopis.model.BookHeaderModel
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.toBookModel
import com.sirekanyan.knigopis.repository.model.FinishedBook
import com.sirekanyan.knigopis.repository.model.PlannedBook

interface BookOrganizer<T> {

    fun sort(books: List<T>): List<T>

    fun group(books: List<T>): List<BookModel>

    fun organize(books: List<T>): List<BookModel> =
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

    override fun group(books: List<PlannedBook>): List<BookModel> {
        val result = mutableListOf<BookModel>()
        val doingBooks = books.filterNot { it.priority == 0 }
        if (doingBooks.isNotEmpty()) {
            val todoHeaderTitle = resources.getString(R.string.books_header_doing)
            val header = BookHeaderModel(todoHeaderTitle, doingBooks.size)
            result.add(header)
            result.addAll(doingBooks.map { it.toBookModel(header.group) })
        }
        val todoBooks = books.filter { it.priority == 0 }
        if (todoBooks.isNotEmpty()) {
            val todoHeaderTitle = resources.getString(R.string.books_header_todo)
            val header = BookHeaderModel(todoHeaderTitle, todoBooks.size)
            result.add(header)
            result.addAll(todoBooks.map { it.toBookModel(header.group) })
        }
        return result
    }

}

class FinishedBookPrepareImpl(
    private val resources: ResourceProvider
) : BookOrganizer<FinishedBook> {

    override fun sort(books: List<FinishedBook>): List<FinishedBook> =
        books.sortedByDescending(FinishedBook::order)

    override fun group(books: List<FinishedBook>): List<BookModel> {
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
                val header = BookHeaderModel(headerTitle, books.size)
                val items = books.map { it.toBookModel(header.group) }
                listOf(header, *items.toTypedArray())
            }
    }

}