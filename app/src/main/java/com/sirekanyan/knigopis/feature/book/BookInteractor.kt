package com.sirekanyan.knigopis.feature.book

import com.sirekanyan.knigopis.MAX_BOOK_PRIORITY
import com.sirekanyan.knigopis.MIN_BOOK_PRIORITY
import com.sirekanyan.knigopis.model.dto.FinishedBookToSend
import com.sirekanyan.knigopis.model.dto.PlannedBookToSend
import com.sirekanyan.knigopis.repository.BookRepository
import io.reactivex.Completable

interface BookInteractor {

    fun saveBook(initialBook: EditBookModel, book: EditBookModel): Completable

}

class BookInteractorImpl(private val repository: BookRepository) : BookInteractor {

    override fun saveBook(initialBook: EditBookModel, book: EditBookModel): Completable =
        if (book.progress == MAX_BOOK_PRIORITY) {
            repository.saveBook(book.id, book.toFinishedBook(), initialBook.isFinished)
        } else {
            repository.saveBook(book.id, book.toPlannedBook(), initialBook.isPlanned)
        }

    private fun EditBookModel.toPlannedBook(): PlannedBookToSend {
        val priority = progress.takeIf { it in (MIN_BOOK_PRIORITY..MAX_BOOK_PRIORITY) }
        return PlannedBookToSend(title, author, notes, priority)
    }

    private fun EditBookModel.toFinishedBook(): FinishedBookToSend {
        return FinishedBookToSend(title, author, date.day, date.month, date.year, notes)
    }

}