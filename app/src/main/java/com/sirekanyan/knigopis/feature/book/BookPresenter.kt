package com.sirekanyan.knigopis.feature.book

import com.sirekanyan.knigopis.MAX_BOOK_PRIORITY
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.toast
import com.sirekanyan.knigopis.common.functions.createBookImageUrl
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.model.BookAction
import com.sirekanyan.knigopis.model.DateModel
import com.sirekanyan.knigopis.model.EditBookModel
import java.util.*

interface BookPresenter : Presenter {

    fun init()

    interface Router {
        fun exit(ok: Boolean = false)
    }

}

class BookPresenterImpl(
    private val router: BookPresenter.Router,
    private val interactor: BookInteractor,
    private val initialBook: EditBookModel
) : BasePresenter<BookView>(),
    BookPresenter,
    BookView.Callbacks {

    private val isNewAction = initialBook.action == BookAction.NEW
    private val isEditAction = initialBook.action == BookAction.EDIT
    private val today = Calendar.getInstance()

    override fun init() {
        view.setTitle(
            if (isEditAction) {
                R.string.book_title_edit
            } else {
                R.string.book_title_add
            }
        )
        view.setBook(initialBook)
        if (isNewAction) {
            view.showKeyboard()
        }
    }

    override fun onNavigationBackClicked() {
        router.exit()
    }

    override fun onSaveOptionClicked(book: EditBookModel) {
        interactor.saveBook(initialBook, book)
            .io2main()
            .doOnSubscribe {
                view.showSaveOption(false)
                view.showSaveProgress(true)
            }
            .doOnError {
                view.showSaveProgress(false)
                view.showSaveOption(true)
            }
            .bind({
                router.exit(ok = true)
            }, {
                view.toast(R.string.book_error_save)
                logError("cannot post planned book", it)
            })
    }

    override fun onTitleFocusRemoved(title: String) {
        view.showBookImage(createBookImageUrl(title))
    }

    override fun onProgressChanged(progress: Int, date: DateModel) {
        view.setBookProgress(progress)
        view.showBookDate(progress == MAX_BOOK_PRIORITY)
        val wasFinished = initialBook.isFinished
        val isFinished = progress == MAX_BOOK_PRIORITY
        if (!wasFinished && isFinished && date.isEmpty()) {
            val year = today.get(Calendar.YEAR)
            val month = if (isEditAction) today.get(Calendar.MONTH).inc() else null
            val day = if (isEditAction) today.get(Calendar.DAY_OF_MONTH) else null
            view.setBookDate(year, month, day)
        }
    }

}