package com.sirekanyan.knigopis.feature.books

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.showProgressBar
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.repository.BookRepository

interface BooksPresenter : Presenter {
    fun refresh()
    fun deleteBook(book: BookDataModel)
    fun showBookActions(book: BookDataModel)
    fun showBookDeleteDialog(book: BookDataModel)
}

class BooksPresenterImpl(
    private val bookRepository: BookRepository
) : BasePresenter<BooksView>(),
    BooksPresenter {

    override fun refresh() {
        bookRepository.observeBooks()
            .io2main()
            .showProgressBar(view)
            .bind({ books ->
                view.updateBooks(books)
            }, {
                logError("cannot load books", it)
                view.showBooksError(it)
            })
    }

    override fun deleteBook(book: BookDataModel) {
        bookRepository.deleteBook(book)
            .io2main()
            .bind({
                refresh()
            }, {
                view.showBookDeleteError()
                logError("cannot delete finished book", it)
            })
    }

    override fun showBookActions(book: BookDataModel) {
        view.showBookActions(book)
    }

    override fun showBookDeleteDialog(book: BookDataModel) {
        view.showBookDeleteDialog(book)
    }

}