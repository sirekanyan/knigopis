package me.vadik.knigopis

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import io.reactivex.rxkotlin.Singles
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

class MainActivity : AppCompatActivity(), Router {

  private val api by lazy { app().baseApi.create(Endpoint::class.java) }
  private val auth by lazy { KAuthImpl(applicationContext, api) as KAuth }
  private val allBooks = mutableListOf<Book>()
  private val booksAdapter by lazy {
    BooksAdapter(BookCoverSearchImpl(
        app().imageApi.create(ImageEndpoint::class.java),
        getSharedPreferences("knigopis", MODE_PRIVATE)
    ), api, auth, this)
  }
  private val allBooksAdapter by lazy { booksAdapter.build(allBooks) }
  private val navigation by lazy { findView<BottomNavigationView>(R.id.navigation) }
  private val fab by lazy { findView<FloatingActionButton>(R.id.add_book_button) }
  private val progressBar by lazy { findView<View>(R.id.books_progress_bar) }
  private val booksPlaceholder by lazy { findView<TextView>(R.id.books_placeholder) }
  private var needUpdate = false
  private lateinit var booksRecyclerView: RecyclerView
  private lateinit var loginOption: MenuItem
  private lateinit var currentTab: CurrentTab

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    booksRecyclerView = initRecyclerView(findView(R.id.books_recycler_view))
    initNavigationView()
    initToolbar(findView(R.id.toolbar))
    fab.setOnClickListener {
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
      refreshCurrentTab(currentTab)
      navigation.selectedItemId = currentTab.itemId
    }
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
    refreshCurrentTab(HOME_TAB)
    navigation.selectedItemId = HOME_TAB.itemId
    navigation.setOnNavigationItemSelectedListener { item ->
      refreshCurrentTab(CurrentTab.getByItemId(item.itemId))
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
          if (auth.isAuthorized()) {
            auth.logout()
          } else {
            startActivityForResult(auth.getTokenRequest(), ULOGIN_REQUEST_CODE)
          }
          refreshOptionsMenu()
          true
        }
        R.id.option_about -> {
          val dialogView = View.inflate(this, R.layout.about, null)
          val versionView = dialogView.findViewById<TextView>(R.id.about_app_version)
          versionView.text = BuildConfig.VERSION_NAME
          AlertDialog.Builder(this).setView(dialogView).show()
          true
        }
        else -> false
      }
    }
    loginOption = toolbar.menu.findItem(R.id.option_login)
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

  private fun refreshCurrentTab(tab: CurrentTab) {
    needUpdate = false
    fab.hide()
    currentTab = tab
    when (tab) {
      HOME_TAB -> refreshHomeTab()
      USERS_TAB -> refreshUsersTab()
      NOTES_TAB -> refreshNotesTab()
    }
  }

  private fun refreshHomeTab() {
    if (progressBar.alpha > 0) {
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
    ).io2main()
        .doOnSubscribe {
          progressBar.fadeIn()
          booksPlaceholder.fadeOut()
        }
        .doAfterTerminate {
          progressBar.fadeOut()
        }
        .subscribe({ (planned, finished) ->
          allBooks.add(BookHeader("К прочтению"))
          allBooks.addAll(planned)
          allBooks.addAll(finished)
          allBooksAdapter.notifyDataSetChanged()
          fab.show()
        }, {
          logError("cannot load books", it)
          booksPlaceholder.setText(
              if (it is HttpException && it.code() == 401) {
                R.string.error_unauthorized
              } else {
                R.string.error_loading_books
              }
          )
          booksPlaceholder.fadeIn()
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
              book.readYear.isEmpty() -> "Прочие года"
              index == 0 -> "Прочитано в $readYear г."
              else -> "$readYear г."
            }
        ))
      }
      groupedBooks.add(book)
      previousReadYear = book.readYear
    }
    return groupedBooks
  }
}
