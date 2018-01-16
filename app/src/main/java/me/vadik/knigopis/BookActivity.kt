package me.vadik.knigopis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.*
import kotlinx.android.synthetic.main.book_edit.*
import me.vadik.knigopis.api.BookCoverSearch
import me.vadik.knigopis.api.BookCoverSearchImpl
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.api.ImageEndpoint
import me.vadik.knigopis.auth.KAuthImpl
import me.vadik.knigopis.model.FinishedBookToSend
import me.vadik.knigopis.model.PlannedBookToSend

private const val IMAGE_PRELOAD_COUNT = 3
private const val EXTRA_BOOK_ID = "me.vadik.knigopis.extra_book_id"
private const val EXTRA_BOOK_FINISHED = "me.vadik.knigopis.extra_book_finished"

fun Context.createNewBookIntent() = Intent(this, BookActivity::class.java)

fun Context.createEditBookIntent(bookId: String, finished: Boolean): Intent =
    Intent(this, BookActivity::class.java)
        .putExtra(EXTRA_BOOK_ID, bookId)
        .putExtra(EXTRA_BOOK_FINISHED, finished)

class BookActivity : AppCompatActivity() {

  private val config by lazy { ConfigurationImpl(applicationContext) as Configuration }
  private val api by lazy { app().baseApi.create(Endpoint::class.java) }
  private val repository by lazy {
    val auth = KAuthImpl(applicationContext, api)
    if (config.isDevMode()) {
      BookRepositoryMock()
    } else {
      BookRepositoryImpl(api, auth)
    }
  }
  private val imageSearch: BookCoverSearch by lazy {
    BookCoverSearchImpl(
        app().imageApi.create(ImageEndpoint::class.java),
        BookCoverCacheImpl(applicationContext)
    )
  }
  private var bookId: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.book_edit)
    bookId = intent.getStringExtra(EXTRA_BOOK_ID)
    toolbar.inflateMenu(R.menu.book_menu)
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
          if (readCheckbox.isChecked) {
            repository.saveBook(bookId, FinishedBookToSend(
                titleEditText.text.toString(),
                authorEditText.text.toString(),
                dayEditText.text.toString(),
                monthEditText.text.toString(),
                yearEditText.text.toString(),
                notesTextArea.text.toString()
            ))
          } else {
            repository.saveBook(bookId, PlannedBookToSend(
                titleEditText.text.toString(),
                authorEditText.text.toString(),
                notesTextArea.text.toString()
            ))
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
    readCheckbox.setOnCheckedChangeListener { _, checked ->
      arrayOf(bookDayInput, bookMonthInput, bookYearInput).forEach { view ->
        view.visibility = if (checked) VISIBLE else GONE
      }
    }
  }

  override fun onStart() {
    super.onStart()
    val finished = intent.getBooleanExtra(EXTRA_BOOK_FINISHED, false)
    bookId?.let { id ->
      if (finished) {
        api.getFinishedBook(id)
            .io2main()
            .doOnSuccess { finishedBook ->
              readCheckbox.isChecked = true
              yearEditText.setText(finishedBook.readYear)
              monthEditText.setText(finishedBook.readMonth)
              dayEditText.setText(finishedBook.readDay)
            }
      } else {
        api.getPlannedBook(id)
            .io2main()
            .doOnSuccess { plannedBook ->
              readCheckbox.isChecked = false
              notesTextArea.setText(plannedBook.notes)
            }
      }.subscribe({ book ->
        titleEditText.setText(book.title)
        authorEditText.setText(book.author)
      }, { logError("cannot get planned book", it) })
    }
  }
}