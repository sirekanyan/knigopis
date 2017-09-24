package me.vadik.knigopis

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import me.vadik.knigopis.CurrentTab.*
import me.vadik.knigopis.adapters.BooksAdapter
import me.vadik.knigopis.adapters.UsersAdapter
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.auth.KAuthImpl
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.User
import me.vadik.knigopis.model.PlannedBook

private const val ULOGIN_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

  private val api by lazy { app().baseApi.create(Endpoint::class.java) }
  private val imageApi by lazy { app().imageApi.create(ImageEndpoint::class.java) }
  private val auth by lazy { KAuthImpl(applicationContext, api) as KAuth }
  private val users = mutableListOf<User>()
  private val finishedBooks = mutableListOf<FinishedBook>()
  private val plannedBooks = mutableListOf<PlannedBook>()
  private val usersAdapter = UsersAdapter.create(users)
  private val finishedBooksAdapter by lazy { BooksAdapter(imageApi).create(finishedBooks) }
  private val plannedBooksAdapter by lazy { BooksAdapter(imageApi).create(plannedBooks) }
  private lateinit var usersView: RecyclerView
  private lateinit var finishedBooksView: RecyclerView
  private lateinit var plannedBooksView: RecyclerView
  private lateinit var loginOption: MenuItem
  private lateinit var currentTab: CurrentTab

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    usersView = initRecyclerView(findView(R.id.users_recycler_view), usersAdapter)
    finishedBooksView = initRecyclerView(findView(R.id.finished_books_view), finishedBooksAdapter)
    plannedBooksView = initRecyclerView(findView(R.id.planned_books_view), plannedBooksAdapter)
    initNavigationView(findView(R.id.navigation))
    initToolbar(findView(R.id.toolbar))
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
    setCurrentTab(if (auth.isAuthorized()) DONE_TAB else HOME_TAB)
    navigation.selectedItemId = currentTab.itemId
    navigation.setOnNavigationItemSelectedListener { item ->
      setCurrentTab(CurrentTab.getByItemId(item.itemId))
      true
    }
  }

  private fun initRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>): RecyclerView {
    recyclerView.adapter = adapter
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
    currentTab = tab
    when (tab) {
      HOME_TAB -> refreshHomeTab()
      DONE_TAB -> refreshDoneTab()
      TODO_TAB -> refreshTodoTab()
    }
  }

  private fun refreshHomeTab() {
    usersView.visibility = View.VISIBLE
    finishedBooksView.visibility = View.GONE
    plannedBooksView.visibility = View.GONE
    api.getLatestUsers()
        .io2main()
        .subscribe({ latestUsers ->
          users.clear()
          users.addAll(latestUsers.values)
          usersAdapter.notifyDataSetChanged()
        }, {
          logError("cannot load users", it)
        })
  }

  private fun refreshDoneTab() {
    usersView.visibility = View.GONE
    finishedBooksView.visibility = View.VISIBLE
    plannedBooksView.visibility = View.GONE
    api.getFinishedBooks(auth.getAccessToken())
        .io2main()
        .subscribe({
          finishedBooks.clear()
          finishedBooks.addAll(it)
          finishedBooksAdapter.notifyDataSetChanged()
        }, {
          logError("cannot load finished books", it)
        })
  }

  private fun refreshTodoTab() {
    usersView.visibility = View.GONE
    finishedBooksView.visibility = View.GONE
    plannedBooksView.visibility = View.VISIBLE
    api.getPlannedBooks(auth.getAccessToken())
        .io2main()
        .subscribe({
          plannedBooks.clear()
          plannedBooks.addAll(it)
          plannedBooksAdapter.notifyDataSetChanged()
        }, {
          logError("cannot load planned books", it)
        })
  }
}
