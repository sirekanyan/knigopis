package me.vadik.knigopis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.android.synthetic.main.book_edit.*
import me.vadik.knigopis.api.BookCoverSearch
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.FinishedBookToSend
import me.vadik.knigopis.model.PlannedBook
import me.vadik.knigopis.model.PlannedBookToSend
import org.koin.android.ext.android.inject
import java.util.*

private const val IMAGE_PRELOAD_COUNT = 3
private const val EXTRA_BOOK_ID = "me.vadik.knigopis.extra_book_id"
private const val EXTRA_BOOK_TITLE = "me.vadik.knigopis.extra_book_title"
private const val EXTRA_BOOK_AUTHOR = "me.vadik.knigopis.extra_book_author"
private const val EXTRA_BOOK_YEAR = "me.vadik.knigopis.extra_book_year"
private const val EXTRA_BOOK_MONTH = "me.vadik.knigopis.extra_book_month"
private const val EXTRA_BOOK_DAY = "me.vadik.knigopis.extra_book_day"
private const val EXTRA_BOOK_NOTES = "me.vadik.knigopis.extra_book_notes"
private const val EXTRA_BOOK_PROGRESS = "me.vadik.knigopis.extra_book_progress"
private const val EXTRA_BOOK_FINISHED = "me.vadik.knigopis.extra_book_finished"

fun Context.createNewBookIntent() = Intent(this, BookActivity::class.java)

fun Context.createNewBookIntent(title: String, author: String): Intent =
    Intent(this, BookActivity::class.java)
        .putExtra(EXTRA_BOOK_TITLE, title)
        .putExtra(EXTRA_BOOK_AUTHOR, author)

fun Context.createEditBookIntent(book: PlannedBook): Intent =
    Intent(this, BookActivity::class.java)
        .putExtra(EXTRA_BOOK_ID, book.id)
        .putExtra(EXTRA_BOOK_TITLE, book.title)
        .putExtra(EXTRA_BOOK_AUTHOR, book.author)
        .putExtra(EXTRA_BOOK_NOTES, book.notes)
        .putExtra(EXTRA_BOOK_PROGRESS, book.priority)
        .putExtra(EXTRA_BOOK_FINISHED, false)

fun Context.createEditBookIntent(book: FinishedBook): Intent =
    Intent(this, BookActivity::class.java)
        .putExtra(EXTRA_BOOK_ID, book.id)
        .putExtra(EXTRA_BOOK_TITLE, book.title)
        .putExtra(EXTRA_BOOK_AUTHOR, book.author)
        .putExtra(EXTRA_BOOK_YEAR, book.readYear)
        .putExtra(EXTRA_BOOK_MONTH, book.readMonth)
        .putExtra(EXTRA_BOOK_DAY, book.readDay)
        .putExtra(EXTRA_BOOK_NOTES, book.notes)
        .putExtra(EXTRA_BOOK_PROGRESS, 100)
        .putExtra(EXTRA_BOOK_FINISHED, true)

class BookActivity : AppCompatActivity() {

    private val repository by inject<BookRepository>()
    private val imageSearch by inject<BookCoverSearch>()
    private val today = Calendar.getInstance()
    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.book_edit)
        bookId = intent.getStringExtra(EXTRA_BOOK_ID)
        toolbar.inflateMenu(R.menu.book_menu)
        if (bookId == null) titleEditText.requestFocus()
        toolbar.setTitle(if (bookId == null) R.string.book_add_title else R.string.book_edit_title)
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
                    if (progressSeekBar.progress == 100) {
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
                                progressSeekBar.progress.takeIf { it in (1..100) }
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
                            toast("Ошибка при сохранении книги")
                            logError("cannot post planned book", it)
                        })
                    true
                }
                else -> false
            }
        }
        coverImageViews.offscreenPageLimit = IMAGE_PRELOAD_COUNT
        titleEditText.setOnFocusChangeListener { _, focus ->
            val editable = titleEditText.editableText
            if (!focus && !editable.isEmpty()) {
                imageSearch.search(editable.toString())
                    .subscribe({ urls ->
                        coverImageViews.visibility = INVISIBLE
                        coverImageViews.adapter = CoverPagerAdapter(urls,
                            onClick = { position, last ->
                                coverImageViews.currentItem = if (last) 0 else position + 1
                            },
                            onFirstLoaded = {
                                coverImageViews.visibility = VISIBLE
                            })
                    }, {
                        logError("cannot load thumbnail", it)
                    })
            }
        }
        progressSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressText.text = "$progress%"
                if (progress == 100) {
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
        titleEditText.setText(intent.getStringExtra(EXTRA_BOOK_TITLE))
        authorEditText.setText(intent.getStringExtra(EXTRA_BOOK_AUTHOR))
        if (bookId != null) {
            progressSeekBar.setProgressSmoothly(intent.getIntExtra(EXTRA_BOOK_PROGRESS, 0))
            notesTextArea.setText(intent.getStringExtra(EXTRA_BOOK_NOTES))
            if (intent.getBooleanExtra(EXTRA_BOOK_FINISHED, false)) {
                yearEditText.setText(intent.getStringExtra(EXTRA_BOOK_YEAR))
                monthEditText.setText(intent.getStringExtra(EXTRA_BOOK_MONTH))
                dayEditText.setText(intent.getStringExtra(EXTRA_BOOK_DAY))
            }
        }
    }
}