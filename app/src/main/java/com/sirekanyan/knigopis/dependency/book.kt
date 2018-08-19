package com.sirekanyan.knigopis.dependency

import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.getRootView
import com.sirekanyan.knigopis.feature.book.*
import com.sirekanyan.knigopis.model.EditBookModel

fun BookActivity.providePresenter(book: EditBookModel): BookPresenter {
    val interactor = BookInteractorImpl(app.bookRepository)
    return BookPresenterImpl(this, interactor, book).also { presenter ->
        presenter.view = BookViewImpl(getRootView(), presenter, book)
    }
}