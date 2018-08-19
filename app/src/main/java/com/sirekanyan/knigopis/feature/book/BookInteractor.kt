package com.sirekanyan.knigopis.feature.book

import com.sirekanyan.knigopis.MAX_BOOK_PRIORITY
import com.sirekanyan.knigopis.model.EditBookModel
import com.sirekanyan.knigopis.model.toFinishedBook
import com.sirekanyan.knigopis.model.toPlannedBook
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

}