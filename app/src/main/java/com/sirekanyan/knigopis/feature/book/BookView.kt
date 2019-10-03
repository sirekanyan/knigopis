package com.sirekanyan.knigopis.feature.book

import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.annotation.StringRes
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.toast.CommonView
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.common.functions.createBookImageUrl
import com.sirekanyan.knigopis.model.DateModel
import com.sirekanyan.knigopis.model.EditBookModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.book_edit.*
import kotlinx.android.synthetic.main.default_app_bar.*

interface BookView : CommonView {

    fun setTitle(@StringRes title: Int)
    fun setBook(book: EditBookModel)
    fun setBookProgress(progress: Int)
    fun setBookDate(year: Int?, month: Int?, day: Int?)
    fun showBookImage(url: String)
    fun showBookDate(isVisible: Boolean)
    fun showSaveOption(isVisible: Boolean)
    fun showSaveProgress(isVisible: Boolean)
    fun showKeyboard()

    interface Callbacks {
        fun onNavigationBackClicked()
        fun onSaveOptionClicked(book: EditBookModel)
        fun onTitleFocusRemoved(title: String)
        fun onProgressChanged(progress: Int, date: DateModel)
    }

}

class BookViewImpl(
    override val containerView: View,
    callbacks: BookView.Callbacks,
    initialBook: EditBookModel
) : BookView,
    LayoutContainer {

    private val context = containerView.context
    private val saveMenuItem: MenuItem
    private val progressMenuItem: MenuItem
    private val bookAction = initialBook.action
    private val bookId = initialBook.id

    init {
        initToolbar(callbacks)
        saveMenuItem = toolbar.menu.findItem(R.id.option_save_book)
        progressMenuItem = toolbar.menu.findItem(R.id.option_progress_bar)
        titleEditText.setOnFocusChangeListener { _, focus ->
            if (!focus) {
                callbacks.onTitleFocusRemoved(titleEditText.editableText.toString())
            }
        }
        progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                callbacks.onProgressChanged(progress, dateModel)
            }
        })
    }

    private val bookModel
        get() = EditBookModel(
            bookAction,
            bookId,
            titleEditText.text.toString(),
            authorEditText.text.toString(),
            progressSeekBar.progress,
            dateModel,
            notesTextArea.text.toString()
        )

    private val dateModel
        get() = DateModel(
            yearEditText.text.toString(),
            monthEditText.text.toString(),
            dayEditText.text.toString()
        )

    override fun setTitle(title: Int) {
        toolbar.setTitle(title)
    }

    override fun setBook(book: EditBookModel) {
        if (book.title.isNotEmpty()) {
            bookImage.showNow()
            bookImage.setSquareImage(createBookImageUrl(book.title))
        }
        titleEditText.setText(book.title)
        authorEditText.setText(book.author)
        progressSeekBar.setProgressSmoothly(book.progress)
        yearEditText.setText(book.date.year)
        monthEditText.setText(book.date.month)
        dayEditText.setText(book.date.day)
        notesTextArea.setText(book.notes)
    }

    override fun setBookProgress(progress: Int) {
        progressText.text = context.getString(R.string.book_progress, progress)
    }

    override fun setBookDate(year: Int?, month: Int?, day: Int?) {
        yearEditText.setText(year?.toString().orEmpty())
        monthEditText.setText(month?.toString().orEmpty())
        dayEditText.setText(day?.toString().orEmpty())
    }

    override fun showBookImage(url: String) {
        context.preloadImage(url, {
            bookImage.showNow()
            bookImage.setSquareImage(url)
        }, {
            bookImage.hideNow()
        })
    }

    override fun showBookDate(isVisible: Boolean) {
        bookDateInputGroup.showNow(isVisible)
    }

    override fun showSaveOption(isVisible: Boolean) {
        saveMenuItem.isVisible = isVisible
    }

    override fun showSaveProgress(isVisible: Boolean) {
        progressMenuItem.isVisible = isVisible
        progressMenuItem.actionView.show(isVisible)
    }

    override fun showKeyboard() {
        titleEditText.requestFocus()
    }

    private fun initToolbar(callbacks: BookView.Callbacks) {
        toolbar.inflateMenu(R.menu.book_menu)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { callbacks.onNavigationBackClicked() }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.option_save_book -> {
                    containerView.hideKeyboard()
                    callbacks.onSaveOptionClicked(bookModel)
                    true
                }
                else -> false
            }
        }
    }

}