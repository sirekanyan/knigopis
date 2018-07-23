package com.sirekanyan.knigopis.feature

import android.content.Context.MODE_PRIVATE
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.sirekanyan.knigopis.BuildConfig
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.extensions.hide
import com.sirekanyan.knigopis.common.extensions.isVisible
import com.sirekanyan.knigopis.common.extensions.show
import com.sirekanyan.knigopis.common.extensions.toast
import com.sirekanyan.knigopis.feature.books.BooksAdapter
import com.sirekanyan.knigopis.feature.notes.NotesAdapter
import com.sirekanyan.knigopis.feature.users.UsersAdapter
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.model.CurrentTab.*
import com.sirekanyan.knigopis.model.NoteModel
import com.sirekanyan.knigopis.model.UserModel
import com.sirekanyan.knigopis.repository.cache.COMMON_PREFS_NAME
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.about.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.books_page.*
import kotlinx.android.synthetic.main.notes_page.*
import kotlinx.android.synthetic.main.users_page.*
import retrofit2.HttpException

interface MainView {

    fun showAboutDialog()
    fun showPage(tab: CurrentTab)
    fun updateBooks(books: List<BookModel>)
    fun updateUsers(users: List<UserModel>)
    fun updateNotes(notes: List<NoteModel>)
    fun showBooksError(it: Throwable)
    fun showUsersError(it: Throwable)
    fun showNotesError(it: Throwable)
    fun showProgress()
    fun hideProgress()
    fun hideSwipeRefresh()

    interface Callbacks {
        fun onLoginOptionClicked()
        fun onProfileOptionClicked()
        fun onAboutOptionClicked()
        fun onDarkThemeOptionClicked(isChecked: Boolean)
        fun onAddBookClicked()
    }

}

class MainViewImpl(
    override val containerView: View,
    private val booksAdapter: BooksAdapter,
    private val usersAdapter: UsersAdapter,
    private val notesAdapter: NotesAdapter
) : MainView, LayoutContainer {

    lateinit var callbacks: MainView.Callbacks
    private val context = containerView.context

    init {
        toolbar.inflateMenu(R.menu.options)
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
        booksRecyclerView.adapter = booksAdapter
        usersRecyclerView.adapter = usersAdapter
        notesRecyclerView.adapter = notesAdapter
        addBookButton.setOnClickListener {
            callbacks.onAddBookClicked()
        }
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
    }

    override fun updateUsers(users: List<UserModel>) {
        usersPlaceholder.show(users.isEmpty())
        usersErrorPlaceholder.hide()
        usersAdapter.submitList(users)
    }

    override fun updateNotes(notes: List<NoteModel>) {
        notesPlaceholder.show(notes.isEmpty())
        notesErrorPlaceholder.hide()
        notesAdapter.submitList(notes)
    }

    override fun showBooksError(it: Throwable) {
        handleError(it, booksPlaceholder, booksErrorPlaceholder, booksAdapter)
    }

    override fun showUsersError(it: Throwable) {
        handleError(it, usersPlaceholder, usersErrorPlaceholder, usersAdapter)
    }

    override fun showNotesError(it: Throwable) {
        handleError(it, notesPlaceholder, notesErrorPlaceholder, notesAdapter)
    }

    override fun showProgress() {
        if (!swipeRefresh.isRefreshing) {
            booksProgressBar.show()
        }
    }

    override fun hideProgress() {
        booksProgressBar.hide()
    }

    override fun hideSwipeRefresh() {
        swipeRefresh.isRefreshing = false
    }

    private fun handleError(
        throwable: Throwable,
        placeholder: View,
        errorPlaceholder: TextView,
        adapter: RecyclerView.Adapter<*>
    ) {
        if (placeholder.isVisible || adapter.itemCount > 0) {
            context.toast(throwable.messageRes)
        } else {
            errorPlaceholder.setText(throwable.messageRes)
            errorPlaceholder.show()
        }
    }

    private val Throwable.messageRes
        get() = if (this is HttpException && code() == 401) {
            R.string.main_error_unauthorized
        } else {
            R.string.common_error_network
        }

}