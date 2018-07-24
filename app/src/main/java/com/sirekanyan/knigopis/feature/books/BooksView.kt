package com.sirekanyan.knigopis.feature.books

import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel

interface BooksView {

    fun updateBooks(books: List<BookModel>)
    fun showBooksError(throwable: Throwable)
    fun showBookActions(book: BookDataModel)
    fun showBookDeleteDialog(book: BookDataModel)
    fun showBookDeleteError()

    interface Callbacks {
        fun onEditBookClicked(book: BookDataModel)
        fun onDeleteBookClicked(book: BookDataModel)
        fun onDeleteBookConfirmed(book: BookDataModel)
        fun onBookClicked(book: BookDataModel)
        fun onBookLongClicked(book: BookDataModel)
    }

}