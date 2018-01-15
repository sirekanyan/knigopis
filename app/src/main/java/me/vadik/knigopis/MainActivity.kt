package me.vadik.knigopis

import android.Manifest.permission.READ_PHONE_STATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.Singles
import kotlinx.android.synthetic.main.activity_main.*
import me.vadik.knigopis.adapters.BooksAdapter
import me.vadik.knigopis.api.BookCoverSearchImpl
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.api.ImageEndpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.auth.KAuthImpl
import me.vadik.knigopis.model.*
import me.vadik.knigopis.model.CurrentTab.*
import retrofit2.HttpException

private const val ULOGIN_REQUEST_CODE = 0
private const val BOOK_REQUEST_CODE = 1
private const val VERSION_CLICK_COUNT_OFF = 1
private const val VERSION_CLICK_COUNT_ON = 12

class MainActivity : AppCompatActivity(), Router {

  private val config by lazy { ConfigurationImpl(applicationContext) as Configuration }
  private val api by lazy { app().baseApi.create(Endpoint::class.java) }
  private val auth by lazy { KAuthImpl(applicationContext, api) as KAuth }
  private val allBooks = mutableListOf<Book>()
  private val booksAdapter by lazy {
    BooksAdapter(BookCoverSearchImpl(
        app().imageApi.create(ImageEndpoint::class.java),
        BookCoverCacheImpl(applicationContext)
    ), api, auth, this)
  }
  private val allBooksAdapter by lazy { booksAdapter.build(allBooks) }
  private val navigation by lazy {
    findView<BottomNavigationView>(R.id.navigation).apply {
      visibility = if (config.isDevMode()) View.VISIBLE else View.GONE
    }
  }
  private var needUpdate = false
  private lateinit var loginOption: MenuItem
  private lateinit var currentTab: CurrentTab

  override fun onCreate(savedInstanceState: Bundle?) {
    if (config.isDevMode()) {
      setTheme(R.style.DevTheme)
    }
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initRecyclerView(booksRecyclerView)
    initNavigationView()
    initToolbar(toolbar)
    addBookButton.setOnClickListener {
      startActivityForResult(createNewBookIntent(), BOOK_REQUEST_CODE)
    }
  }

  override fun onStart() {
    super.onStart()
    refreshOptionsMenu()
    auth.requestAccessToken {
      refreshOptionsMenu()
    }
    if (needUpdate) {
      refresh()
    }
    booksPlaceholder.setOnClickListener { refresh() }
  }

  override fun onStop() {
    booksPlaceholder.setOnClickListener(null)
    super.onStop()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      ULOGIN_REQUEST_CODE -> {
        if (resultCode == RESULT_OK && data != null) {
          auth.saveTokenResponse(data)
        }
      }
      BOOK_REQUEST_CODE -> {
        needUpdate = resultCode == RESULT_OK
      }
    }
  }

  override fun openEditBookScreen(book: Book) {
    startActivityForResult(createEditBookIntent(book.id, book is FinishedBook), BOOK_REQUEST_CODE)
  }

  private fun initNavigationView() {
    refresh(HOME_TAB)
    navigation.setOnNavigationItemSelectedListener { item ->
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
        R.id.option_about -> {
          val dialogView = View.inflate(this, R.layout.about, null)
          val versionView = dialogView.findViewById<TextView>(R.id.about_app_version)
          versionView.text = BuildConfig.VERSION_NAME
          var count = 0
          val enabled = config.isDevMode()
          val max = if (enabled) {
            VERSION_CLICK_COUNT_OFF
          } else {
            VERSION_CLICK_COUNT_ON
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
                startActivity(Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                ))
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
    if (auth.isAuthorized()) {
      loginOption.setTitle(R.string.option_logout)
      loginOption.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
    } else {
      loginOption.setTitle(R.string.option_login)
      loginOption.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }
  }

  private fun refresh(tab: CurrentTab = currentTab) {
    setCurrentTab(tab)
    navigation.selectedItemId = tab.itemId
  }

  private fun setCurrentTab(tab: CurrentTab) {
    needUpdate = false
    addBookButton.hide()
    currentTab = tab
    when (tab) {
      HOME_TAB -> refreshHomeTab()
      USERS_TAB -> refreshUsersTab()
      NOTES_TAB -> refreshNotesTab()
    }
  }

  private fun refreshHomeTab() {
    if (booksProgressBar.alpha > 0) {
      return
    }
    booksRecyclerView.adapter = allBooksAdapter
    allBooks.clear()
    Singles.zip(
        api.getPlannedBooks(auth.getAccessToken())
            .map { it.sortedByDescending(PlannedBook::priority) },
        api.getFinishedBooks(auth.getAccessToken())
            .map { it.sortedByDescending(FinishedBook::order) }
            .map { it.groupFinishedBooks() }
    ).map { (planned, finished) ->
      mutableListOf<Book>().apply {
        if (planned.isNotEmpty()) {
          add(BookHeader(getString(R.string.book_header_todo)))
        }
        addAll(planned)
        addAll(finished)
      }
    }.io2main()
        .doOnSubscribe {
          booksProgressBar.show()
          booksPlaceholder.hide()
        }
        .doAfterTerminate {
          booksProgressBar.hide()
        }
        .subscribe({ books ->
          if (books.isEmpty()) {
            booksPlaceholder.setText(R.string.error_no_books)
            booksPlaceholder.show()
          }
          allBooks.addAll(books)
          allBooksAdapter.notifyDataSetChanged()
          addBookButton.show()
        }, {
          logError("cannot load books", it)
          booksPlaceholder.setText(
              if (it is HttpException && it.code() == 401) {
                R.string.error_unauthorized
              } else {
                R.string.error_loading_books
              }
          )
          booksPlaceholder.show()
        })
  }

  private fun refreshUsersTab() {
    // todo
  }

  private fun refreshNotesTab() {
    // todo
  }

  private fun List<FinishedBook>.groupFinishedBooks(): List<Book> {
    val groupedBooks = mutableListOf<Book>()
    var previousReadYear = Int.MAX_VALUE.toString()
    forEachIndexed { index, book ->
      val readYear = book.readYear
      if (previousReadYear != readYear) {
        groupedBooks.add(BookHeader(
            when {
              book.readYear.isEmpty() -> getString(R.string.book_header_done_other)
              index == 0 -> getString(R.string.book_header_done_first, readYear)
              else -> getString(R.string.book_header_done, readYear)
            }
        ))
      }
      groupedBooks.add(book)
      previousReadYear = book.readYear
    }
    return groupedBooks
  }
}
