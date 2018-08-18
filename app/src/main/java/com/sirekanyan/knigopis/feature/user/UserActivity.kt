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
import com.sirekanyan.knigopis.common.android.dialog.createDialogItem
import com.sirekanyan.knigopis.common.android.header.HeaderItemDecoration
import com.sirekanyan.knigopis.common.android.header.StickyHeaderImpl
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.common.functions.extra
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.dependency.provideDialogs
import com.sirekanyan.knigopis.dependency.provideInteractor
import com.sirekanyan.knigopis.feature.book.createBookIntent
import com.sirekanyan.knigopis.feature.book.createDoneBook
import com.sirekanyan.knigopis.feature.book.createTodoBook
import com.sirekanyan.knigopis.model.BookDataModel
import kotlinx.android.synthetic.main.user_activity.*

private val EXTRA_USER_ID = extra("user_id")
private val EXTRA_USER_NAME = extra("user_name")
private val EXTRA_USER_PHOTO = extra("user_photo")

fun Context.createUserIntent(id: String, name: String, avatar: String?): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, id)
        .putExtra(EXTRA_USER_NAME, name)
        .putExtra(EXTRA_USER_PHOTO, avatar)

class UserActivity : BaseActivity() {

    private val interactor by lazy { provideInteractor() }
    private val dialogs by lazy { provideDialogs() }
    private val userId by lazy { intent.getStringExtra(EXTRA_USER_ID) }
    private val userName by lazy { intent.getStringExtra(EXTRA_USER_NAME) }
    private val userPhoto by lazy { intent.getStringExtra(EXTRA_USER_PHOTO) }
    private val booksAdapter = UserBooksAdapter(::onBookLongClicked)
    private lateinit var unsubscribeOption: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        setDarkTheme(app.config.isDarkTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)
        toolbar.title = userName
        userImage.setCircleImage(userPhoto, R.drawable.oval_dark_placeholder_background)
        userImage.setElevationRes(R.dimen.image_view_elevation)
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
        userBooksRecyclerView.addItemDecoration(HeaderItemDecoration(StickyHeaderImpl(booksAdapter)))
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
                val notes = getString(R.string.book_notes_copied, userName)
                val todoBook = createTodoBook(book.title, book.author, notes)
                startActivity(createBookIntent(todoBook))
            },
            createDialogItem(R.string.user_button_done, R.drawable.ic_playlist_add_check) {
                val doneBook = createDoneBook(book.title, book.author)
                startActivity(createBookIntent(doneBook))
            }
        )
    }

}