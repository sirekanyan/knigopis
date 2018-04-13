package me.vadik.knigopis

import android.Manifest.permission.READ_PHONE_STATE
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.about.view.*
import kotlinx.android.synthetic.main.activity_main.*
import me.vadik.knigopis.adapters.BooksAdapter
import me.vadik.knigopis.adapters.notes.NotesAdapter
import me.vadik.knigopis.adapters.users.UsersAdapter
import me.vadik.knigopis.api.BookCoverSearch
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.dialog.DialogFactory
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.CurrentTab
import me.vadik.knigopis.model.CurrentTab.*
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.PlannedBook
import me.vadik.knigopis.model.note.Identity
import me.vadik.knigopis.model.note.Note
import me.vadik.knigopis.model.subscription.Subscription
import me.vadik.knigopis.user.createUserIntent
import org.koin.android.ext.android.inject
import retrofit2.HttpException

private const val ULOGIN_REQUEST_CODE = 0
private const val BOOK_REQUEST_CODE = 1
private const val VERSION_CLICK_COUNT_OFF = 1
private const val VERSION_CLICK_COUNT_ON = 12
private const val CURRENT_TAB_KEY = "me.vadik.knigopis.current_tab"

class MainActivity : AppCompatActivity(), Router {

    private val api by inject<Endpoint>()
    private val bookCoverSearch by inject<BookCoverSearch>()
    private val config by inject<Configuration>()
    private val auth by inject<KAuth>()
    private val dialogs by inject<DialogFactory> { mapOf("activity" to this) }
    private val bookRepository by inject<BookRepository>()
    private val allBooks = mutableListOf<Book>()
    private val allUsers = mutableListOf<Subscription>()
    private val allNotes = mutableListOf<Note>()
    private val booksAdapter by lazy { BooksAdapter(bookCoverSearch, api, auth, this, dialogs) }
    private val allBooksAdapter by lazy { booksAdapter.build(allBooks) }
    private val usersAdapter by lazy { UsersAdapter(allUsers, this, dialogs) }
    private val notesAdapter by lazy { NotesAdapter(allNotes, this) }
    private var userLoggedIn = false
    private var booksChanged = false
    private lateinit var loginOption: MenuItem
    private lateinit var profileOption: MenuItem
    private lateinit var currentTab: CurrentTab

    override fun onCreate(savedInstanceState: Bundle?) {
        if (config.isDevMode()) {
            setTheme(R.style.DevTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView(booksRecyclerView)
        initRecyclerView(usersRecyclerView)
        initRecyclerView(notesRecyclerView)
        val currentTabId = savedInstanceState?.getInt(CURRENT_TAB_KEY)
        val currentTab = currentTabId?.let { CurrentTab.getByItemId(it) }
        initNavigationView(currentTab)
        initToolbar(toolbar)
        addBookButton.setOnClickListener {
            startActivityForResult(createNewBookIntent(), BOOK_REQUEST_CODE)
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
        auth.requestAccessToken {
            refreshOptionsMenu()
            if (userLoggedIn) {
                userLoggedIn = false
                refresh()
            }
        }
        if (booksChanged) {
            booksChanged = false
            refresh()
        }
        intent.data?.also {
            val normalizedUri = Uri.parse(it.toString().replaceFirst("/#/", "/"))
            normalizedUri.getQueryParameter("u")?.let { userId ->
                api.createSubscription(userId, auth.getAccessToken())
                    .io2main()
                    .subscribe({
                        toast("Successfully subscribed")
                    }, {
                        logError("Cannot create subscription", it)
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

    override fun openEditBookScreen(book: Book) {
        when (book) {
            is PlannedBook -> startActivityForResult(createEditBookIntent(book), BOOK_REQUEST_CODE)
            is FinishedBook -> startActivityForResult(createEditBookIntent(book), BOOK_REQUEST_CODE)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun openUserScreen(user: Subscription) {
        startActivity(createUserIntent(user))
    }

    override fun openUserScreen(user: Identity) {
        startActivity(createUserIntent(user))
    }

    override fun openBrowser(uri: Uri) {
        startActivityOrElse(Intent(ACTION_VIEW, uri)) {
            toast("Невозможно открыть страницу")
        }
    }

    override fun shareProfile(url: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, url)
        startActivity(
            Intent.createChooser(
                sharingIntent,
                getString(R.string.option_share_title)
            )
        )
    }

    override fun unsubscribe(userId: String) {
        api.deleteSubscription(userId, auth.getAccessToken())
            .io2main()
            .subscribe({
                refresh(isForce = true)
            }, {
                handleNetworkError("Cannot unsubscribe", it)
            })
    }

    private fun initNavigationView(currentTab: CurrentTab?) {
        val defaultTab = if (auth.isAuthorized()) HOME_TAB else NOTES_TAB
        refresh(currentTab ?: defaultTab)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            setCurrentTab(CurrentTab.getByItemId(item.itemId))
            true
        }
    }

    private fun initRecyclerView(recyclerView: RecyclerView): RecyclerView {
        recyclerView.layoutManager = LinearLayoutManager(this)
        return recyclerView
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
                    api.getProfile(auth.getAccessToken())
                        .io2main()
                        .subscribe({ user ->
                            AlertDialog.Builder(this)
                                .setTitle("Мой профиль")
                                .setMessage(
                                    """
                                        Имя: ${user.nickname ?: "(не задано)"}
                                        Книг: ${user.booksCount}
                                        Подписок: ${user.subscriptions?.size ?: 0}
                                        Создан: ${DateUtils.getRelativeTimeSpanString(user.fixedCreatedAt.time)}
                                        Обновлен: ${DateUtils.getRelativeTimeSpanString(user.fixedUpdatedAt.time)}
                                    """.trimIndent()
                                )
                                .setPositiveButton("Поделиться") { _, _ ->
                                    shareProfile(user.fixedProfile)
                                }
                                .setNegativeButton("Закрыть", null)
                                .show()
                        }, {
                            logError("Cannot get profile", it)
                        })
                    true
                }
                R.id.option_about -> {
                    val dialogView = View.inflate(this, R.layout.about, null)
                    val versionView = dialogView.aboutAppVersion
                    val designerView = dialogView.aboutDesignerText
                    versionView.text = BuildConfig.VERSION_NAME
                    var count = 0
                    val enabled = config.isDevMode()
                    val max = if (enabled) {
                        VERSION_CLICK_COUNT_OFF
                    } else {
                        VERSION_CLICK_COUNT_ON
                    }
                    if (enabled) {
                        designerView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_about_designer_highlighted, 0, 0, 0
                        )
                    }
                    versionView.setOnClickListener {
                        if (++count == max) {
                            enabled.not().let {
                                if (it) toast(R.string.dev_mode_message)
                                config.setDevMode(it)
                                recreate()
                            }
                        }
                    }
                    AlertDialog.Builder(this).setView(dialogView).show()
                    true
                }
                else -> false
            }
        }
        loginOption = toolbar.menu.findItem(R.id.option_login)
        profileOption = toolbar.menu.findItem(R.id.option_profile)
    }

    private fun login() {
        RxPermissions(this).requestEach(READ_PHONE_STATE).subscribe({
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
                        .setTitle(R.string.no_access)
                        .setMessage(R.string.no_access_message)
                        .setPositiveButton(R.string.no_access_retry_button) { _, _ ->
                            login()
                        }
                        .setNegativeButton(R.string.dialog_cancel_button, null)
                        .setCancelable(false)
                        .show()
                }
                else -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.no_permissions)
                        .setMessage(R.string.no_permissions_message)
                        .setPositiveButton(R.string.no_permissions_goto_settings_button) { _, _ ->
                            startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                )
                            )
                        }
                        .setNegativeButton(R.string.dialog_cancel_button, null)
                        .setCancelable(false)
                        .show()
                }
            }
        }, {
            logError("cannot request permission", it)
        })
    }

    private fun refreshOptionsMenu() {
        loginOption.isVisible = true
        profileOption.isVisible = auth.isAuthorized()
        if (auth.isAuthorized()) {
            loginOption.setTitle(R.string.option_logout)
            loginOption.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        } else {
            loginOption.setTitle(R.string.option_login)
            loginOption.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
    }

    private fun refresh(tab: CurrentTab = currentTab, isForce: Boolean = false) {
        setCurrentTab(tab, isForce)
        bottomNavigation.selectedItemId = tab.itemId
    }

    private fun setCurrentTab(tab: CurrentTab, isForce: Boolean = false) {
        addBookButton.hide()
        currentTab = tab
        toggleRecyclerView(tab)
        val isFirst = isFirstOpenTab(tab)
        if (isFirst) {
            when (tab) {
                HOME_TAB -> booksRecyclerView.adapter = allBooksAdapter
                USERS_TAB -> usersRecyclerView.adapter = usersAdapter
                NOTES_TAB -> notesRecyclerView.adapter = notesAdapter
            }
        }
        if (isFirst || isForce) {
            when (tab) {
                HOME_TAB -> refreshHomeTab()
                USERS_TAB -> refreshUsersTab()
                NOTES_TAB -> refreshNotesTab()
            }
        }
    }

    private fun isFirstOpenTab(tab: CurrentTab) =
        when (tab) {
            HOME_TAB -> booksRecyclerView.adapter == null
            USERS_TAB -> usersRecyclerView.adapter == null
            NOTES_TAB -> notesRecyclerView.adapter == null
        }

    private fun toggleRecyclerView(tab: CurrentTab) {
        usersRecyclerView.show(tab == USERS_TAB)
        notesRecyclerView.show(tab == NOTES_TAB)
        booksRecyclerView.show(tab == HOME_TAB)
    }

    private fun refreshHomeTab() {
        if (booksProgressBar.alpha > 0) {
            return
        }
        bookRepository.loadBooks()
            .io2main()
            .doOnSubscribe {
                if (!swipeRefresh.isRefreshing) {
                    booksProgressBar.show()
                }
                booksPlaceholder.hide()
            }
            .doFinally {
                booksProgressBar.hide()
                swipeRefresh.isRefreshing = false
            }
            .subscribe({ books ->
                if (books.isEmpty()) {
                    booksPlaceholder.setText(R.string.error_no_books)
                    booksPlaceholder.show()
                }
                allBooks.clear()
                allBooks.addAll(books)
                allBooksAdapter.notifyDataSetChanged()
                addBookButton.show()
            }, {
                handleNetworkError("cannot load books", it)
            })
    }

    private fun refreshUsersTab() {
        api.getSubscriptions(auth.getAccessToken())
            .io2main()
            .doOnSubscribe {
                if (!swipeRefresh.isRefreshing) {
                    booksProgressBar.show()
                }
                booksPlaceholder.hide()
            }
            .doFinally {
                booksProgressBar.hide()
                swipeRefresh.isRefreshing = false
            }
            .subscribe({ subscriptions ->
                allUsers.clear()
                allUsers.addAll(subscriptions)
                usersAdapter.notifyDataSetChanged()
            }, {
                handleNetworkError("cannot load users", it)
            })
    }

    private fun refreshNotesTab() {
        api.getLatestBooksWithNotes()
            .io2main()
            .doOnSubscribe {
                if (!swipeRefresh.isRefreshing) {
                    booksProgressBar.show()
                }
                booksPlaceholder.hide()
            }
            .doFinally {
                booksProgressBar.hide()
                swipeRefresh.isRefreshing = false
            }
            .subscribe({ notes ->
                allNotes.clear()
                allNotes.addAll(notes.values)
                notesAdapter.notifyDataSetChanged()
            }, {
                handleNetworkError("cannot load notes", it)
            })
    }

    private fun handleNetworkError(message: String, throwable: Throwable) {
        logError(message, throwable)
        toast(
            if (throwable is HttpException && throwable.code() == 401) {
                R.string.error_unauthorized
            } else {
                R.string.error_loading_books
            }
        )
    }

}
