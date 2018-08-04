package com.sirekanyan.knigopis.feature

import android.content.Context.MODE_PRIVATE
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.sirekanyan.knigopis.BuildConfig
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.dialog.DialogFactory
import com.sirekanyan.knigopis.common.android.dialog.createDialogItem
import com.sirekanyan.knigopis.common.android.header.HeaderItemDecoration
import com.sirekanyan.knigopis.common.android.header.StickyHeaderImpl
import com.sirekanyan.knigopis.common.extensions.getFullTitleString
import com.sirekanyan.knigopis.common.extensions.hide
import com.sirekanyan.knigopis.common.extensions.show
import com.sirekanyan.knigopis.common.extensions.toast
import com.sirekanyan.knigopis.common.functions.handleError
import com.sirekanyan.knigopis.feature.books.BooksAdapter
import com.sirekanyan.knigopis.feature.books.BooksView
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.model.CurrentTab.*
import com.sirekanyan.knigopis.repository.cache.COMMON_PREFS_NAME
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.about.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.books_page.*
import kotlinx.android.synthetic.main.notes_page.*
import kotlinx.android.synthetic.main.users_page.*

interface MainView : BooksView {

    fun showAboutDialog()
    fun showPage(tab: CurrentTab)
    fun showNavigation(isVisible: Boolean)
    fun setNavigation(itemId: Int)
    fun showLoginOption(isVisible: Boolean)
    fun showProfileOption(isVisible: Boolean)
    fun setDarkThemeOptionChecked(isChecked: Boolean)

    interface Callbacks : BooksView.Callbacks {
        fun onNavigationClicked(itemId: Int)
        fun onToolbarClicked()
        fun onLoginOptionClicked()
        fun onProfileOptionClicked()
        fun onAboutOptionClicked()
        fun onDarkThemeOptionClicked(isChecked: Boolean)
        fun onAddBookClicked()
        fun onRefreshSwiped()
    }

}

class MainViewImpl(
    override val containerView: View,
    private val callbacks: MainView.Callbacks,
    private val dialogs: DialogFactory
) : MainView, LayoutContainer {

    private val context = containerView.context
    private val resources = context.resources
    private val booksAdapter = BooksAdapter(callbacks::onBookClicked, callbacks::onBookLongClicked)
    private val loginOption: MenuItem
    private val profileOption: MenuItem
    private val darkThemeOption: MenuItem

    init {
        toolbar.inflateMenu(R.menu.options)
        toolbar.setOnClickListener {
            callbacks.onToolbarClicked()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.option_login -> {
                    callbacks.onLoginOptionClicked()
                    true
                }
                R.id.option_profile -> {
                    callbacks.onProfileOptionClicked()
                    true
                }
                R.id.option_about -> {
                    callbacks.onAboutOptionClicked()
                    true
                }
                R.id.option_dark_theme -> {
                    item.isChecked = !item.isChecked
                    callbacks.onDarkThemeOptionClicked(item.isChecked)
                    true
                }
                R.id.option_clear_cache -> {
                    context.cacheDir.deleteRecursively()
                    context.getSharedPreferences(COMMON_PREFS_NAME, MODE_PRIVATE)
                        .edit().clear().apply()
                    true
                }
                else -> false
            }
        }
        loginOption = toolbar.menu.findItem(R.id.option_login)
        profileOption = toolbar.menu.findItem(R.id.option_profile)
        darkThemeOption = toolbar.menu.findItem(R.id.option_dark_theme)
        toolbar.menu.findItem(R.id.option_clear_cache).isVisible = BuildConfig.DEBUG
        booksRecyclerView.adapter = booksAdapter
        booksRecyclerView.addItemDecoration(HeaderItemDecoration(StickyHeaderImpl(booksAdapter)))
        addBookButton.setOnClickListener {
            callbacks.onAddBookClicked()
        }
        booksRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    dy > 0 -> addBookButton.hide()
                    dy < 0 -> addBookButton.show()
                }
            }
        })
        swipeRefresh.setOnRefreshListener(callbacks::onRefreshSwiped)
    }

    override fun showAboutDialog() {
        val dialogView = View.inflate(context, R.layout.about, null)
        dialogView.aboutAppVersion.text = BuildConfig.VERSION_NAME
        AlertDialog.Builder(context).setView(dialogView).show()
    }

    override fun showPage(tab: CurrentTab) {
        booksPage.show(tab == HOME_TAB)
        usersPage.show(tab == USERS_TAB)
        notesPage.show(tab == NOTES_TAB)
    }

    override fun updateBooks(books: List<BookModel>) {
        booksPlaceholder.show(books.isEmpty())
        booksErrorPlaceholder.hide()
        booksAdapter.submitList(books)
        callbacks.onBooksUpdated()
    }

    override fun showBooksError(throwable: Throwable) {
        handleError(throwable, booksPlaceholder, booksErrorPlaceholder, booksAdapter)
    }

    override fun showNavigation(isVisible: Boolean) {
        if (isVisible) {
            bottomNavigation.show()
            bottomNavigation.setOnNavigationItemSelectedListener { item ->
                callbacks.onNavigationClicked(item.itemId)
                true
            }
        } else {
            bottomNavigation.hide()
            bottomNavigation.setOnNavigationItemSelectedListener(null)
        }
    }

    override fun setNavigation(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }

    override fun showLoginOption(isVisible: Boolean) {
        loginOption.isVisible = isVisible
    }

    override fun showProfileOption(isVisible: Boolean) {
        profileOption.isVisible = isVisible
    }

    override fun setDarkThemeOptionChecked(isChecked: Boolean) {
        darkThemeOption.isChecked = isChecked
    }

    override fun showBookActions(book: BookDataModel) {
        val bookFullTitle = resources.getFullTitleString(book.title, book.author)
        dialogs.showDialog(
            bookFullTitle,
            createDialogItem(R.string.books_button_edit, R.drawable.ic_edit) {
                callbacks.onEditBookClicked(book)
            },
            createDialogItem(R.string.books_button_delete, R.drawable.ic_delete) {
                callbacks.onDeleteBookClicked(book)
            }
        )
    }

    override fun showBookDeleteDialog(book: BookDataModel) {
        val bookFullTitle = resources.getFullTitleString(book.title, book.author)
        AlertDialog.Builder(context)
            .setTitle(R.string.books_title_confirm_delete)
            .setMessage(context.getString(R.string.books_message_confirm_delete, bookFullTitle))
            .setNegativeButton(R.string.common_button_cancel) { d, _ -> d.dismiss() }
            .setPositiveButton(R.string.books_button_confirm_delete) { d, _ ->
                callbacks.onDeleteBookConfirmed(book)
                d.dismiss()
            }
            .show()
    }

    override fun showBookDeleteError() {
        context.toast(R.string.books_error_delete)
    }

}