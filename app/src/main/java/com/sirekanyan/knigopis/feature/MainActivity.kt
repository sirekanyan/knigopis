package com.sirekanyan.knigopis.feature

import android.Manifest.permission.READ_PHONE_STATE
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.sirekanyan.knigopis.BuildConfig
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.Router
import com.sirekanyan.knigopis.common.BaseActivity
import com.sirekanyan.knigopis.common.ResourceProvider
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.common.view.dialog.DialogFactory
import com.sirekanyan.knigopis.common.view.dialog.DialogItem
import com.sirekanyan.knigopis.common.view.dialog.createDialogItem
import com.sirekanyan.knigopis.common.view.header.HeaderItemDecoration
import com.sirekanyan.knigopis.common.view.header.StickyHeaderImpl
import com.sirekanyan.knigopis.feature.book.createEditBookIntent
import com.sirekanyan.knigopis.feature.book.createNewBookIntent
import com.sirekanyan.knigopis.feature.books.BooksAdapter
import com.sirekanyan.knigopis.feature.notes.NotesAdapter
import com.sirekanyan.knigopis.feature.profile.createProfileIntent
import com.sirekanyan.knigopis.feature.user.createUserIntent
import com.sirekanyan.knigopis.feature.users.UriItem
import com.sirekanyan.knigopis.feature.users.UsersAdapter
import com.sirekanyan.knigopis.model.*
import com.sirekanyan.knigopis.model.CurrentTab.*
import com.sirekanyan.knigopis.repository.*
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.about.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.books_page.*
import kotlinx.android.synthetic.main.notes_page.*
import kotlinx.android.synthetic.main.users_page.*
import org.koin.android.ext.android.inject
import retrofit2.HttpException

private const val ULOGIN_REQUEST_CODE = 0
private const val BOOK_REQUEST_CODE = 1
private const val CURRENT_TAB_KEY = "current_tab"

class MainActivity : BaseActivity(), Router {

    private val api by inject<Endpoint>()
    private val config by inject<Configuration>()
    private val auth by inject<KAuth>()
    private val dialogs by inject<DialogFactory> { mapOf("activity" to this) }
    private val bookRepository by inject<BookRepository>()
    private val userRepository by inject<UserRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val resourceProvider by inject<ResourceProvider>()
    private val allBooks = mutableListOf<BookModel>()
    private val booksAdapter by lazy { BooksAdapter(::onBookClicked, ::onBookLongClicked) }
    private val usersAdapter by lazy { UsersAdapter(::onUserClicked, ::onUserLongClicked) }
    private val notesAdapter by lazy { NotesAdapter(::onNoteClicked) }
    private val loadedTabs = mutableSetOf<CurrentTab>()
    private var userLoggedIn = false
    private var booksChanged = false
    private lateinit var loginOption: MenuItem
    private lateinit var profileOption: MenuItem
    private lateinit var currentTab: CurrentTab

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (config.isDarkTheme) R.style.DarkAppTheme else R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        booksRecyclerView.adapter = booksAdapter
        usersRecyclerView.adapter = usersAdapter
        notesRecyclerView.adapter = notesAdapter
        booksRecyclerView.addItemDecoration(HeaderItemDecoration(StickyHeaderImpl(allBooks)))
        val currentTabId = savedInstanceState?.getInt(CURRENT_TAB_KEY)
        val currentTab = currentTabId?.let { CurrentTab.getByItemId(it) }
        val defaultTab = if (auth.isAuthorized()) HOME_TAB else NOTES_TAB
        refresh(currentTab ?: defaultTab)
        initNavigationView()
        initToolbar(toolbar)
        addBookButton.setOnClickListener {
            startActivityForResult(
                createNewBookIntent(),
                BOOK_REQUEST_CODE
            )
        }
        booksRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    dy > 0 -> addBookButton.hide()
                    dy < 0 -> addBookButton.show()
                }
            }
        })
        swipeRefresh.setOnRefreshListener {
            refresh(isForce = true)
        }
    }

    override fun onStart() {
        super.onStart()
        refreshOptionsMenu()
        auth.requestAccessToken().bind({
            refreshOptionsMenu()
            if (userLoggedIn) {
                userLoggedIn = false
                refresh()
            }
        }, {
            logError("cannot check credentials", it)
        })
        if (booksChanged) {
            booksChanged = false
            refresh(isForce = true)
        }
        intent.data?.also { userUrl ->
            intent.data = null
            val normalizedUri = Uri.parse(userUrl.toString().replaceFirst("/#/", "/"))
            normalizedUri.getQueryParameter("u")?.let { userId ->
                api.getUser(userId)
                    .io2main()
                    .bind({ user ->
                        openUserScreen(userId, user.name, user.photo)
                    }, {
                        logError("Cannot get user", it)
                    })
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(CURRENT_TAB_KEY, currentTab.itemId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ULOGIN_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    auth.saveTokenResponse(data)
                    userLoggedIn = true
                }
            }
            BOOK_REQUEST_CODE -> {
                booksChanged = resultCode == RESULT_OK
            }
        }
    }

    override fun openBookScreen(book: BookDataModel) {
        startActivityForResult(createEditBookIntent(book), BOOK_REQUEST_CODE)
    }

    override fun openUserScreen(id: String, name: String, avatar: String?) {
        startActivity(createUserIntent(id, name, avatar))
    }

    override fun openWebPage(uri: Uri) {
        startActivityOrNull(Intent(ACTION_VIEW, uri)) ?: toast(R.string.users_info_no_browser)
    }

    override fun onBackPressed() {
        if (currentTab == HOME_TAB || !auth.isAuthorized()) {
            super.onBackPressed()
        } else {
            refresh(HOME_TAB)
        }
    }

    private fun initNavigationView() {
        if (auth.isAuthorized()) {
            bottomNavigation.show()
            bottomNavigation.setOnNavigationItemSelectedListener { item ->
                setCurrentTab(CurrentTab.getByItemId(item.itemId))
                true
            }
        } else {
            bottomNavigation.hide()
            bottomNavigation.setOnNavigationItemSelectedListener(null)
        }
    }

    private fun initToolbar(toolbar: Toolbar) {
        toolbar.inflateMenu(R.menu.options)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.option_login -> {
                    login()
                    true
                }
                R.id.option_profile -> {
                    startActivity(createProfileIntent())
                    true
                }
                R.id.option_about -> {
                    val dialogView = View.inflate(this, R.layout.about, null)
                    dialogView.aboutAppVersion.text = BuildConfig.VERSION_NAME
                    AlertDialog.Builder(this).setView(dialogView).show()
                    true
                }
                R.id.option_dark_theme -> {
                    item.isChecked = !item.isChecked
                    config.isDarkTheme = item.isChecked
                    recreate()
                    true
                }
                R.id.option_clear_cache -> {
                    getSharedPreferences("cached", MODE_PRIVATE).edit().clear().apply()
                    cacheDir.deleteRecursively()
                    true
                }
                else -> false
            }
        }
        loginOption = toolbar.menu.findItem(R.id.option_login)
        profileOption = toolbar.menu.findItem(R.id.option_profile)
        val darkThemeOption = toolbar.menu.findItem(R.id.option_dark_theme)
        darkThemeOption.isChecked = config.isDarkTheme
        val clearCacheOption = toolbar.menu.findItem(R.id.option_clear_cache)
        clearCacheOption.isVisible = BuildConfig.DEBUG
        toolbar.setOnClickListener {
            if (currentTab == HOME_TAB) {
                config.sortingMode = if (config.sortingMode == 0) 1 else 0
                refresh(isForce = true)
            }
        }
    }

    private fun login() {
        RxPermissions(this).requestEach(READ_PHONE_STATE).bind({
            when {
                it.granted -> {
                    if (auth.isAuthorized()) {
                        auth.logout()
                        refresh()
                    } else {
                        startActivityForResult(auth.getTokenRequest(), ULOGIN_REQUEST_CODE)
                    }
                    refreshOptionsMenu()
                }
                it.shouldShowRequestPermissionRationale -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.permissions_title_no_access)
                        .setMessage(R.string.permissions_message_no_access)
                        .setPositiveButton(R.string.common_button_retry) { _, _ ->
                            login()
                        }
                        .setNegativeButton(R.string.common_button_cancel, null)
                        .setCancelable(false)
                        .show()
                }
                else -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.permissions_title_request)
                        .setMessage(R.string.permissions_message_request)
                        .setPositiveButton(R.string.permissions_button_settings) { _, _ ->
                            startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                )
                            )
                        }
                        .setNegativeButton(R.string.common_button_cancel, null)
                        .setCancelable(false)
                        .show()
                }
            }
        }, {
            logError("cannot request permission", it)
        })
    }

    private fun refreshOptionsMenu() {
        initNavigationView()
        auth.isAuthorized().let { authorized ->
            loginOption.isVisible = !authorized
            profileOption.isVisible = authorized
        }
    }

    private fun refresh(tab: CurrentTab = currentTab, isForce: Boolean = false) {
        val t = if (auth.isAuthorized()) tab else NOTES_TAB
        setCurrentTab(t, isForce)
        bottomNavigation.selectedItemId = t.itemId
    }

    private fun setCurrentTab(tab: CurrentTab, isForce: Boolean = false) {
        currentTab = tab
        booksPage.show(tab == HOME_TAB)
        usersPage.show(tab == USERS_TAB)
        notesPage.show(tab == NOTES_TAB)
        val isFirst = !loadedTabs.contains(tab)
        if (isFirst || isForce) {
            when (tab) {
                HOME_TAB -> refreshHomeTab(tab)
                USERS_TAB -> refreshUsersTab(tab)
                NOTES_TAB -> refreshNotesTab(tab)
            }
        }
    }

    private fun refreshHomeTab(tab: CurrentTab) {
        bookRepository.observeBooks()
            .io2main()
            .showProgressBar()
            .bind({ books ->
                booksPlaceholder.show(books.isEmpty())
                booksErrorPlaceholder.hide()
                allBooks.clear()
                allBooks.addAll(books)
                booksAdapter.submitList(books)
                loadedTabs.add(tab)
            }, {
                logError("cannot load books", it)
                handleError(it, booksPlaceholder, booksErrorPlaceholder, booksAdapter)
            })
    }

    private fun refreshUsersTab(tab: CurrentTab) {
        userRepository.observeUsers()
            .io2main()
            .showProgressBar()
            .bind({ users ->
                usersPlaceholder.show(users.isEmpty())
                usersErrorPlaceholder.hide()
                usersAdapter.submitList(users)
                loadedTabs.add(tab)
            }, {
                logError("cannot load users", it)
                handleError(it, usersPlaceholder, usersErrorPlaceholder, usersAdapter)
            })
    }

    private fun refreshNotesTab(tab: CurrentTab) {
        noteRepository.observeNotes()
            .io2main()
            .showProgressBar()
            .bind({ notes ->
                notesPlaceholder.show(notes.isEmpty())
                notesErrorPlaceholder.hide()
                notesAdapter.submitList(notes)
                loadedTabs.add(tab)
            }, {
                logError("cannot load notes", it)
                handleError(it, notesPlaceholder, notesErrorPlaceholder, notesAdapter)
            })
    }

    private fun <T> Flowable<T>.showProgressBar(): Flowable<T> =
        doOnSubscribe {
            if (!swipeRefresh.isRefreshing) {
                booksProgressBar.show()
            }
        }.doOnNext {
            booksProgressBar.hide()
        }.doFinally {
            booksProgressBar.hide()
            swipeRefresh.isRefreshing = false
        }

    private fun handleError(
        th: Throwable,
        placeholder: View,
        errPlaceholder: TextView,
        adapter: RecyclerView.Adapter<*>
    ) {
        if (placeholder.isVisible || adapter.itemCount > 0) {
            toast(th.messageRes)
        } else {
            errPlaceholder.setText(th.messageRes)
            errPlaceholder.show()
        }
    }

    private val Throwable.messageRes
        get() = if (this is HttpException && code() == 401) {
            R.string.main_error_unauthorized
        } else {
            R.string.common_error_network
        }

    private fun onBookClicked(book: BookDataModel) {
        openBookScreen(book)
    }

    private fun onBookLongClicked(book: BookDataModel) {
        val bookFullTitle = resources.getFullTitleString(book.title, book.author)
        val onDeleteConfirmed = {
            val index = allBooks.indexOfFirst { it.id == book.id }
            if (index >= 0) {
                if (book.isFinished) {
                    api.deleteFinishedBook(book.id, auth.getAccessToken())
                } else {
                    api.deletePlannedBook(book.id, auth.getAccessToken())
                }
                    .io2main()
                    .bind({
                        refresh(isForce = true)
                    }, {
                        toast(R.string.books_error_delete)
                        logError("cannot delete finished book", it)
                    })
            }
        }
        val onDeleteClicked = {
            AlertDialog.Builder(this)
                .setTitle(R.string.books_title_confirm_delete)
                .setMessage(
                    getString(
                        R.string.books_message_confirm_delete,
                        bookFullTitle
                    )
                )
                .setNegativeButton(R.string.common_button_cancel) { d, _ -> d.dismiss() }
                .setPositiveButton(R.string.books_button_confirm_delete) { d, _ ->
                    onDeleteConfirmed()
                    d.dismiss()
                }
                .show()
        }
        dialogs.showDialog(
            bookFullTitle,
            createDialogItem(R.string.books_button_edit, R.drawable.ic_edit) {
                openBookScreen(book)
            },
            createDialogItem(R.string.books_button_delete, R.drawable.ic_delete) {
                onDeleteClicked()
            }
        )
    }

    private fun onUserClicked(user: UserModel) {
        openUserScreen(user.id, user.name, user.image)
    }

    private fun onUserLongClicked(user: UserModel) {
        val dialogItems: List<DialogItem> = user.profiles
            .mapNotNull(String::toUriOrNull)
            .map { UriItem(it, resourceProvider) }
            .distinctBy(UriItem::title)
            .map { uriItem ->
                createDialogItem(uriItem.title, uriItem.iconRes) {
                    openWebPage(uriItem.uri)
                }
            }
        dialogs.showDialog(user.name, *dialogItems.toTypedArray())
    }

    private fun onNoteClicked(note: NoteModel) {
        openUserScreen(note.userId, note.userName, note.userImage)
    }

}
