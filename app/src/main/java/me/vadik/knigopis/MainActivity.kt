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
import io.reactivex.Single
import me.vadik.knigopis.adapters.BooksAdapter
import me.vadik.knigopis.api.BookCoverSearchImpl
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.api.ImageEndpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.auth.KAuthImpl
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.BookHeader
import me.vadik.knigopis.model.CurrentTab
import me.vadik.knigopis.model.CurrentTab.*
import me.vadik.knigopis.model.FinishedBook

private const val ULOGIN_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

  private val api by lazy { app().baseApi.create(Endpoint::class.java) }
  private val auth by lazy { KAuthImpl(applicationContext, api) as KAuth }
  private val allBooks = mutableListOf<Book>()
  private val finishedBooks = mutableListOf<Book>()
  private val plannedBooks = mutableListOf<Book>()
  private val booksAdapter by lazy {
    BooksAdapter(BookCoverSearchImpl(
        app().imageApi.create(ImageEndpoint::class.java),
        getSharedPreferences("knigopis", MODE_PRIVATE)
    ), api, auth)
  }
  private val allBooksAdapter by lazy { booksAdapter.build(allBooks) }
  private val finishedBooksAdapter by lazy { booksAdapter.build(finishedBooks) }
  private val plannedBooksAdapter by lazy { booksAdapter.build(plannedBooks) }
  private val fab by lazy { findView<FloatingActionButton>(R.id.add_book_button) }
  private lateinit var booksRecyclerView: RecyclerView
  private lateinit var loginOption: MenuItem
  private lateinit var currentTab: CurrentTab

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    booksRecyclerView = initRecyclerView(findView(R.id.books_recycler_view))
    initNavigationView(findView(R.id.navigation))
    initToolbar(findView(R.id.toolbar))
    fab.setOnClickListener {
      startActivity(createNewBookIntent())
    }
  }

  override fun onStart() {
    super.onStart()
    refreshOptionsMenu()
    auth.requestAccessToken {
      refreshOptionsMenu()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      ULOGIN_REQUEST_CODE -> {
        if (resultCode == RESULT_OK && data != null) {
          auth.saveTokenResponse(data)
        }
      }
    }
  }

  private fun initNavigationView(navigation: BottomNavigationView) {
    setCurrentTab(HOME_TAB)
    navigation.selectedItemId = currentTab.itemId
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

  private fun setCurrentTab(tab: CurrentTab) {
    if (tab == HOME_TAB) fab.show() else fab.hide()
    currentTab = tab
    when (tab) {
      HOME_TAB -> refreshHomeTab()
      USERS_TAB -> refreshUsersTab()
      NOTES_TAB -> refreshNotesTab()
    }
  }

  private fun refreshHomeTab() {
    booksRecyclerView.adapter = allBooksAdapter
    allBooks.clear()
    Single.concat(
        Single.just(listOf(BookHeader("К прочтению"))),
        api.getPlannedBooks(auth.getAccessToken())
            .map { it.sortedByDescending { it.priority } },
        api.getFinishedBooks(auth.getAccessToken())
            .map { it.sortedByDescending(FinishedBook::order) }
            .map { it.groupFinishedBooks() }
    ).io2main()
        .subscribe({
          allBooks.addAll(it)
          allBooksAdapter.notifyDataSetChanged()
        }, {
          toast("Не удалось загрузить книги")
          logError("cannot load books", it)
        })
  }

  private fun refreshUsersTab() {
    booksRecyclerView.adapter = finishedBooksAdapter
    api.getFinishedBooks(auth.getAccessToken())
        .io2main()
        .subscribe({
          finishedBooks.clear()
          finishedBooks.addAll(it.sortedByDescending(FinishedBook::order))
          finishedBooksAdapter.notifyDataSetChanged()
        }, {
          logError("cannot load finished books", it)
        })
  }

  private fun refreshNotesTab() {
    booksRecyclerView.adapter = plannedBooksAdapter
    api.getPlannedBooks(auth.getAccessToken())
        .io2main()
        .subscribe({
          plannedBooks.clear()
          plannedBooks.addAll(it.sortedByDescending { it.priority })
          plannedBooksAdapter.notifyDataSetChanged()
        }, {
          logError("cannot load planned books", it)
        })
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
