package me.vadik.knigopis.feature.user

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.user_activity.*
import me.vadik.knigopis.*
import me.vadik.knigopis.common.*
import me.vadik.knigopis.common.view.header.HeaderItemDecoration
import me.vadik.knigopis.common.view.header.StickyHeaderInterface
import me.vadik.knigopis.repository.Configuration
import me.vadik.knigopis.common.view.dialog.DialogFactory
import me.vadik.knigopis.repository.model.Book
import me.vadik.knigopis.repository.model.BookHeader
import me.vadik.knigopis.common.extensions.*
import org.koin.android.ext.android.inject

private const val EXTRA_USER_ID = "me.vadik.knigopis.extra_user_id"
private const val EXTRA_USER_NAME = "me.vadik.knigopis.extra_user_name"
private const val EXTRA_USER_PHOTO = "me.vadik.knigopis.extra_user_photo"

fun Context.createUserIntent(id: String, name: String, avatar: String?): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, id)
        .putExtra(EXTRA_USER_NAME, name)
        .putExtra(EXTRA_USER_PHOTO, avatar)

class UserActivity : AppCompatActivity() {

    private val config by inject<Configuration>()
    private val interactor by inject<UserInteractor>()
    private val dialogs by inject<DialogFactory> { mapOf("activity" to this) }
    private val userId by lazy { intent.getStringExtra(EXTRA_USER_ID) }
    private val books = mutableListOf<Book>()
    private val bookHeaders = mutableListOf<BookHeader>()
    private val booksAdapter = BooksAdapter(books, dialogs)
    private lateinit var unsubscribeOption: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        if (config.isDarkTheme) {
            setTheme(R.style.DarkAppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)
        toolbar.title = intent.getStringExtra(EXTRA_USER_NAME)
        toolbarImage.setCircleImage(intent.getStringExtra(EXTRA_USER_PHOTO))
        toolbarImage.setElevationRes(R.dimen.image_view_elevation)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            fab.isSelected = true
            fab.setImageResource(R.drawable.ic_done)
            fab.setOnClickListener(null)
            interactor.subscribe(userId)
                .subscribe({
                    view.snackbar(R.string.users_info_subscribed)
                }, {
                    fab.showScale()
                    logError("Cannot update subscription", it)
                })
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val layoutManager = LinearLayoutManager(this)
        userBooksRecyclerView.layoutManager = layoutManager
        userBooksRecyclerView.addItemDecoration(
            HeaderItemDecoration(
                object : StickyHeaderInterface {
                    override fun getHeaderPositionForItem(itemPosition: Int): Int {
                        return itemPosition
                    }

                    override fun getHeaderLayout(headerPosition: Int): Int {
                        return R.layout.header
                    }

                    override fun bindHeaderData(header: View, headerPosition: Int) {
                        val book = bookHeaders[headerPosition]
                        val title = book.title.let {
                            if (it.isEmpty()) {
                                getString(R.string.books_header_done_other)
                            } else {
                                it
                            }
                        }
                        header.findViewById<TextView>(R.id.book_title).text = title
                        header.findViewById<TextView>(R.id.books_count).text =
                                resources.getQuantityString(
                                    R.plurals.common_header_books,
                                    book.count,
                                    book.count
                                )
                        header.findViewById<TextView>(R.id.books_count).showNow()
                        header.findViewById<View>(R.id.header_bottom_divider).showNow()
                    }

                    override fun isHeader(itemPosition: Int): Boolean {
                        return books[itemPosition] is BookHeader
                    }
                }
            )
        )
        userBooksRecyclerView.adapter = booksAdapter
    }

    override fun onStart() {
        super.onStart()
        interactor.getBooks(userId)
            .doOnSubscribe {
                userBooksProgressBar.showNow()
                userBooksErrorPlaceholder.hideNow()
                userBooksRecyclerView.hideNow()
            }
            .doFinally { userBooksProgressBar.hide() }
            .doOnSuccess { userBooksRecyclerView.show() }
            .doOnError { userBooksErrorPlaceholder.show() }
            .subscribe({
                books.clear()
                books.addAll(it.map { it.first })
                bookHeaders.clear()
                bookHeaders.addAll(it.map { it.second })
                booksAdapter.notifyDataSetChanged()
            }, {
                logError("Cannot load user books", it)
            })
        interactor.isSubscribed(userId)
            .subscribe({ isSubscribed ->
                if (isSubscribed) {
                    unsubscribeOption.isVisible = true
                } else {
                    fab.showScale()
                }
            }, {
                logError("Cannot update subscription", it)
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        unsubscribeOption = menu.findItem(R.id.option_unsubscribe)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_copy -> {
                val link = "http://www.knigopis.com/#/user/books?u=$userId"
                systemClipboardManager.primaryClip = ClipData.newPlainText(null, link)
                toast(R.string.user_info_copied, link)
                true
            }
            R.id.option_unsubscribe -> {
                interactor.unsubscribe(userId)
                    .subscribe({}, {
                        logError("Cannot unsubscribe", it)
                        toast(R.string.user_error_unsubscribe)
                    })
                true
            }
            else -> false
        }
    }
}