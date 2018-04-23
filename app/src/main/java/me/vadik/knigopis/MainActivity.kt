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
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import kotlinx.android.synthetic.main.about.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.books_page.*
import kotlinx.android.synthetic.main.notes_page.*
import kotlinx.android.synthetic.main.users_page.*
import me.vadik.knigopis.adapters.BooksAdapter
import me.vadik.knigopis.adapters.notes.NotesAdapter
import me.vadik.knigopis.adapters.users.UsersAdapter
import me.vadik.knigopis.api.BookCoverSearch
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.common.ResourceProvider
import me.vadik.knigopis.dialog.DialogFactory
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.CurrentTab
import me.vadik.knigopis.model.CurrentTab.*
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.PlannedBook
import me.vadik.knigopis.model.note.Identity
import me.vadik.knigopis.model.note.Note
import me.vadik.knigopis.model.subscription.Subscription
import me.vadik.knigopis.profile.createProfileIntent
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
    private val resources by inject<ResourceProvider>()
    private val allBooks = mutableListOf<Book>()
    private val allUsers = mutableListOf<Subscription>()
    private val allNotes = mutableListOf<Note>()
    private val booksAdapter by lazy { BooksAdapter(bookCoverSearch, api, auth, this, dialogs) }
    private val allBooksAdapter by lazy { booksAdapter.build(allBooks) }
    private val usersAdapter by lazy { UsersAdapter(allUsers, this, dialogs, resources) }
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
        val defaultTab = if (auth.isAuthorized()) HOME_TAB else NOTES_TAB
        refresh(currentTab ?: defaultTab)
        initNavigationView()
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
            refresh(isForce = true)
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
                    startActivity(createProfileIntent())
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
        togglePage(tab)
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
            HOME_TAB -> booksRecyclerView.adapter == null || booksErrorPlaceholder.isVisible
            USERS_TAB -> usersRecyclerView.adapter == null || usersErrorPlaceholder.isVisible
            NOTES_TAB -> notesRecyclerView.adapter == null || notesErrorPlaceholder.isVisible
        }

    private fun togglePage(tab: CurrentTab) {
        booksPage.show(tab == HOME_TAB)
        usersPage.show(tab == USERS_TAB)
        notesPage.show(tab == NOTES_TAB)
    }

    private fun refreshHomeTab() {
        bookRepository.loadBooks()
            .io2main()
            .showProgressBar()
            .subscribe({ books ->
                booksPlaceholder.show(books.isEmpty())
                booksErrorPlaceholder.hide()
                allBooks.clear()
                allBooks.addAll(books)
                allBooksAdapter.notifyDataSetChanged()
            }, {
                logError("cannot load books", it)
                handleError(it, booksPlaceholder, booksErrorPlaceholder, allBooksAdapter)
            })
    }

    private fun refreshUsersTab() {
        api.getSubscriptions(auth.getAccessToken())
            .io2main()
            .showProgressBar()
            .subscribe({ subscriptions ->
                usersPlaceholder.show(subscriptions.isEmpty())
                usersErrorPlaceholder.hide()
                allUsers.clear()
                allUsers.addAll(subscriptions)
                usersAdapter.notifyDataSetChanged()
            }, {
                logError("cannot load users", it)
                handleError(it, usersPlaceholder, usersErrorPlaceholder, usersAdapter)
            })
    }

    private fun refreshNotesTab() {
        api.getLatestBooksWithNotes()
            .io2main()
            .showProgressBar()
            .subscribe({ notes ->
                notesPlaceholder.show(notes.isEmpty())
                notesErrorPlaceholder.hide()
                allNotes.clear()
                allNotes.addAll(notes.values)
                notesAdapter.notifyDataSetChanged()
            }, {
                logError("cannot load notes", it)
                handleError(it, notesPlaceholder, notesErrorPlaceholder, notesAdapter)
            })
    }

    private fun <T> Single<T>.showProgressBar() =
        doOnSubscribe {
            if (!swipeRefresh.isRefreshing) {
                booksProgressBar.show()
            }
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
            R.string.error_unauthorized
        } else {
            R.string.error_loading_data
        }

}
