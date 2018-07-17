package com.sirekanyan.knigopis.feature.book

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.*
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.dto.FinishedBookToSend
import com.sirekanyan.knigopis.model.dto.PlannedBookToSend
import com.sirekanyan.knigopis.repository.BookRepository
import com.sirekanyan.knigopis.repository.Configuration
import com.sirekanyan.knigopis.repository.api.createBookImageUrl
import kotlinx.android.synthetic.main.book_edit.*
import org.koin.android.ext.android.inject
import java.util.*

private val EXTRA_BOOK_ID = extra("book_id")
private val EXTRA_BOOK_TITLE = extra("book_title")
private val EXTRA_BOOK_AUTHOR = extra("book_author")
private val EXTRA_BOOK_YEAR = extra("book_year")
private val EXTRA_BOOK_MONTH = extra("book_month")
private val EXTRA_BOOK_DAY = extra("book_day")
private val EXTRA_BOOK_NOTES = extra("book_notes")
private val EXTRA_BOOK_PROGRESS = extra("book_progress")
private val EXTRA_BOOK_FINISHED = extra("book_finished")

fun Context.createNewBookIntent() = Intent(this, BookActivity::class.java)

fun Context.createNewBookIntent(title: String, author: String, progress: Int? = null): Intent =
    Intent(this, BookActivity::class.java)
        .putExtra(EXTRA_BOOK_TITLE, title)
        .putExtra(EXTRA_BOOK_AUTHOR, author)
        .putExtra(EXTRA_BOOK_PROGRESS, progress)

fun Context.createEditBookIntent(book: BookDataModel): Intent =
    Intent(this, BookActivity::class.java)
        .putExtra(EXTRA_BOOK_ID, book.id)
        .putExtra(EXTRA_BOOK_TITLE, book.title)
        .putExtra(EXTRA_BOOK_AUTHOR, book.author)
        .putExtra(EXTRA_BOOK_FINISHED, book.isFinished)
        .putExtra(EXTRA_BOOK_NOTES, book.notes)
        .apply {
            if (book.isFinished) {
                putExtra(EXTRA_BOOK_YEAR, book.date?.year)
                putExtra(EXTRA_BOOK_MONTH, book.date?.month)
                putExtra(EXTRA_BOOK_DAY, book.date?.day)
                putExtra(EXTRA_BOOK_PROGRESS, MAX_BOOK_PRIORITY)
            } else {
                putExtra(EXTRA_BOOK_PROGRESS, book.priority)
            }
        }

class BookActivity : AppCompatActivity() {

    private val config by inject<Configuration>()
    private val repository by inject<BookRepository>()
    private val today = Calendar.getInstance()
    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (config.isDarkTheme) {
            setTheme(R.style.DarkAppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.book_edit)
        bookId = intent.getStringExtra(EXTRA_BOOK_ID)
        toolbar.inflateMenu(R.menu.book_menu)
        if (bookId == null) titleEditText.requestFocus()
        toolbar.setTitle(if (bookId == null) R.string.book_title_add else R.string.book_title_edit)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val progressMenuItem = toolbar.menu.findItem(R.id.option_progress_bar)
        toolbar.setOnMenuItemClickListener { saveMenuItem ->
            when (saveMenuItem.itemId) {
                R.id.option_save_book -> {
                    hideKeyboard()
                    val wasFinished = intent.getBooleanExtra(EXTRA_BOOK_FINISHED, false)
                        .takeUnless { bookId == null }
                    if (progressSeekBar.progress == MAX_BOOK_PRIORITY) {
                        repository.saveBook(
                            bookId,
                            FinishedBookToSend(
                                titleEditText.text.toString(),
                                authorEditText.text.toString(),
                                dayEditText.text.toString(),
                                monthEditText.text.toString(),
                                yearEditText.text.toString(),
                                notesTextArea.text.toString()
                            ),
                            wasFinished
                        )
                    } else {
                        repository.saveBook(
                            bookId,
                            PlannedBookToSend(
                                titleEditText.text.toString(),
                                authorEditText.text.toString(),
                                notesTextArea.text.toString(),
                                progressSeekBar.progress.takeIf { it in (MIN_BOOK_PRIORITY..MAX_BOOK_PRIORITY) }
                            ),
                            wasFinished
                        )
                    }.io2main()
                        .doOnSubscribe {
                            saveMenuItem.isVisible = false
                            progressMenuItem.isVisible = true
                            progressMenuItem.actionView.show()
                        }
                        .doOnError {
                            progressMenuItem.isVisible = false
                            progressMenuItem.actionView.hide()
                            saveMenuItem.isVisible = true
                        }
                        .subscribe({
                            setResult(RESULT_OK)
                            finish()
                        }, {
                            toast(R.string.book_error_save)
                            logError("cannot post planned book", it)
                        })
                    true
                }
                else -> false
            }
        }
        titleEditText.setOnFocusChangeListener { _, focus ->
            val editable = titleEditText.editableText
            if (!focus) {
                val url = createBookImageUrl(editable.toString())
                preloadImage(url, {
                    bookImage.showNow()
                    bookImage.setSquareImage(url)
                }, {
                    bookImage.hideNow()
                })
            }
        }
        progressSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressText.text = "$progress%"
                if (progress == MAX_BOOK_PRIORITY) {
                    bookDateInputGroup.showNow()
                    if (yearEditText.text.isEmpty() && monthEditText.text.isEmpty() && dayEditText.text.isEmpty()) {
                        yearEditText.setText(today.get(Calendar.YEAR).toString())
                        if (bookId != null) {
                            monthEditText.setText(today.get(Calendar.MONTH).inc().toString())
                            dayEditText.setText(today.get(Calendar.DAY_OF_MONTH).toString())
                        }
                    }
                } else {
                    bookDateInputGroup.hideNow()
                }
            }
        })
        intent.getStringExtra(EXTRA_BOOK_TITLE)?.let { title ->
            titleEditText.setText(title)
            bookImage.showNow()
            bookImage.setSquareImage(createBookImageUrl(title))
        }
        authorEditText.setText(intent.getStringExtra(EXTRA_BOOK_AUTHOR))
        progressSeekBar.setProgressSmoothly(intent.getIntExtra(EXTRA_BOOK_PROGRESS, 0))
        if (bookId != null) {
            notesTextArea.setText(intent.getStringExtra(EXTRA_BOOK_NOTES))
            if (intent.getBooleanExtra(EXTRA_BOOK_FINISHED, false)) {
                yearEditText.setText(intent.getStringExtra(EXTRA_BOOK_YEAR))
                monthEditText.setText(intent.getStringExtra(EXTRA_BOOK_MONTH))
                dayEditText.setText(intent.getStringExtra(EXTRA_BOOK_DAY))
            }
        }
    }
}