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
import me.vadik.knigopis.adapters.WishesAdapter
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.auth.KAuthImpl
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.User
import me.vadik.knigopis.model.Wish
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ULOGIN_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

  private val api by lazy { app().retrofit.create(Endpoint::class.java) }
  private val auth by lazy { KAuthImpl(applicationContext, api) as KAuth }
  private val users = mutableListOf<User>()
  private val books = mutableListOf<Book>()
  private val wishes = mutableListOf<Wish>()
  private val usersAdapter = UsersAdapter(users)
  private val booksAdapter = BooksAdapter(books)
  private val wishesAdapter = WishesAdapter(wishes)
  private lateinit var usersRecyclerView: RecyclerView
  private lateinit var booksRecyclerView: RecyclerView
  private lateinit var wishesRecyclerView: RecyclerView
  private lateinit var loginOption: MenuItem
  private lateinit var currentTab: CurrentTab

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    usersRecyclerView = initRecyclerView(findView(R.id.users_recycler_view), usersAdapter)
    booksRecyclerView = initRecyclerView(findView(R.id.books_recycler_view), booksAdapter)
    wishesRecyclerView = initRecyclerView(findView(R.id.wishes_recycler_view), wishesAdapter)
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
    usersRecyclerView.visibility = View.VISIBLE
    booksRecyclerView.visibility = View.GONE
    wishesRecyclerView.visibility = View.GONE
    api.getLatestUsers().enqueue(object : Callback<Map<String, User>> {
      override fun onResponse(call: Call<Map<String, User>>?, response: Response<Map<String, User>>?) {
        users.clear()
        response?.body()?.values?.forEach { user ->
          users.add(user)
        }
        usersAdapter.notifyDataSetChanged()
      }

      override fun onFailure(call: Call<Map<String, User>>?, t: Throwable?) {
        logError("cannot load users", t)
      }
    })
  }

  private fun refreshDoneTab() {
    usersRecyclerView.visibility = View.GONE
    booksRecyclerView.visibility = View.VISIBLE
    wishesRecyclerView.visibility = View.GONE
    api.getBooks(auth.getAccessToken()).enqueue(object : Callback<List<Book>> {
      override fun onResponse(call: Call<List<Book>>?, response: Response<List<Book>>?) {
        books.clear()
        response?.body()?.forEach { book ->
          books.add(book)
        }
        usersAdapter.notifyDataSetChanged()
      }

      override fun onFailure(call: Call<List<Book>>?, t: Throwable?) {
        logError("cannot load books", t)
      }
    })
  }

  private fun refreshTodoTab() {
    usersRecyclerView.visibility = View.GONE
    booksRecyclerView.visibility = View.GONE
    wishesRecyclerView.visibility = View.VISIBLE
    api.getWishes(auth.getAccessToken()).enqueue(object : Callback<List<Wish>> {
      override fun onResponse(call: Call<List<Wish>>?, response: Response<List<Wish>>?) {
        wishes.clear()
        response?.body()?.forEach { wish ->
          wishes.add(wish)
        }
        wishesAdapter.notifyDataSetChanged()
      }

      override fun onFailure(call: Call<List<Wish>>?, t: Throwable?) {
        logError("cannot load wishes", t)
      }
    })
  }
}
