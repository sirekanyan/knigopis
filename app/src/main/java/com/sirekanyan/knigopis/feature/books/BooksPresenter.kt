package com.sirekanyan.knigopis.feature.books

import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.showProgressBar
import com.sirekanyan.knigopis.common.extensions.toast
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.feature.PagePresenter
import com.sirekanyan.knigopis.feature.PagesPresenter
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.repository.BookRepository

interface BooksPresenter : PagePresenter {

    interface Router {
        fun openNewBookScreen()
        fun openBookScreen(book: BookDataModel)
    }

}

class BooksPresenterImpl(
    private val router: BooksPresenter.Router,
    private val bookRepository: BookRepository
) : BasePresenter<BooksView>(),
    BooksPresenter,
    BooksView.Callbacks {

    lateinit var parent: PagesPresenter

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

    override fun onAddBookClicked() {
        router.openNewBookScreen()
    }

    override fun onBookClicked(book: BookDataModel) {
        router.openBookScreen(book)
    }

    override fun onBookLongClicked(book: BookDataModel) {
        view.showBookActions(book)
    }

    override fun onEditBookClicked(book: BookDataModel) {
        router.openBookScreen(book)
    }

    override fun onDeleteBookClicked(book: BookDataModel) {
        view.showBookDeleteDialog(book)
    }

    override fun onDeleteBookConfirmed(book: BookDataModel) {
        bookRepository.deleteBook(book)
            .io2main()
            .bind({
                refresh()
            }, {
                view.toast(R.string.books_error_delete)
                logError("cannot delete finished book", it)
            })
    }

    override fun onBooksUpdated() {
        parent.onPageUpdated(CurrentTab.BOOKS_TAB)
    }

}