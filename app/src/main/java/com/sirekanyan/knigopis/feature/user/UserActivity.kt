package com.sirekanyan.knigopis.feature.user

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BaseActivity
import com.sirekanyan.knigopis.common.MAX_BOOK_PRIORITY
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.common.functions.extra
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.common.view.dialog.DialogFactory
import com.sirekanyan.knigopis.common.view.dialog.createDialogItem
import com.sirekanyan.knigopis.common.view.header.HeaderItemDecoration
import com.sirekanyan.knigopis.common.view.header.StickyHeaderImpl
import com.sirekanyan.knigopis.createParameters
import com.sirekanyan.knigopis.feature.book.createNewBookIntent
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.repository.Configuration
import kotlinx.android.synthetic.main.user_activity.*
import org.koin.android.ext.android.inject

private val EXTRA_USER_ID = extra("user_id")
private val EXTRA_USER_NAME = extra("user_name")
private val EXTRA_USER_PHOTO = extra("user_photo")

fun Context.createUserIntent(id: String, name: String, avatar: String?): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, id)
        .putExtra(EXTRA_USER_NAME, name)
        .putExtra(EXTRA_USER_PHOTO, avatar)

class UserActivity : BaseActivity() {

    private val config by inject<Configuration>()
    private val interactor by inject<UserInteractor>()
    private val dialogs by inject<DialogFactory>(parameters = createParameters())
    private val userId by lazy { intent.getStringExtra(EXTRA_USER_ID) }
    private val books = mutableListOf<BookModel>()
    private val booksAdapter = UserBooksAdapter(::onBookLongClicked)
    private lateinit var unsubscribeOption: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        if (config.isDarkTheme) {
            setTheme(R.style.DarkAppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)
        toolbar.title = intent.getStringExtra(EXTRA_USER_NAME)
        toolbarImage.setCircleImage(
            intent.getStringExtra(EXTRA_USER_PHOTO),
            R.drawable.oval_dark_placeholder_background
        )
        toolbarImage.setElevationRes(R.dimen.image_view_elevation)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            interactor.addFriend(userId)
                .doOnSubscribe { fab.startCollapseAnimation() }
                .doFinally { fab.startExpandAnimation() }
                .bind({
                    fab.setOnClickListener(null)
                    fab.isSelected = true
                    fab.setImageResource(R.drawable.ic_done)
                }, {
                    logError("Cannot update subscription", it)
                    view.snackbar(R.string.common_error_network)
                    fab.isSelected = false
                    fab.setImageResource(R.drawable.ic_person_add)
                })
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val layoutManager = LinearLayoutManager(this)
        userBooksRecyclerView.layoutManager = layoutManager
        userBooksRecyclerView.addItemDecoration(HeaderItemDecoration(StickyHeaderImpl(books)))
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
            .bind({
                books.clear()
                books.addAll(it)
                booksAdapter.submitList(it)
                onBooksLoaded()
            }, {
                logError("Cannot load user books", it)
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        unsubscribeOption = menu.findItem(R.id.option_unsubscribe)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.option_copy -> {
                val link = "http://www.knigopis.com/#/user/books?u=$userId"
                systemClipboardManager.primaryClip = ClipData.newPlainText(null, link)
                toast(R.string.user_info_copied, link)
                true
            }
            R.id.option_unsubscribe -> {
                interactor.removeFriend(userId)
                    .bind({}, {
                        logError("Cannot unsubscribe", it)
                        toast(R.string.user_error_unsubscribe)
                    })
                true
            }
            else -> false
        }
    }

    private fun onBooksLoaded() {
        interactor.isFriend(userId)
            .bind({ isSubscribed ->
                if (isSubscribed) {
                    unsubscribeOption.isVisible = true
                } else {
                    fab.showNow()
                    fab.startExpandAnimation()
                }
            }, {
                logError("Cannot check subscription", it)
            })
    }

    private fun onBookLongClicked(book: BookDataModel) {
        dialogs.showDialog(
            resources.getFullTitleString(book.title, book.author),
            createDialogItem(R.string.user_button_todo, R.drawable.ic_playlist_add) {
                startActivity(createNewBookIntent(book.title, book.author))
            },
            createDialogItem(R.string.user_button_done, R.drawable.ic_playlist_add_check) {
                startActivity(createNewBookIntent(book.title, book.author, MAX_BOOK_PRIORITY))
            }
        )
    }

}