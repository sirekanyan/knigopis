package me.vadik.knigopis

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import me.vadik.knigopis.CurrentTab.DONE_TAB
import me.vadik.knigopis.CurrentTab.HOME_TAB
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.auth.KAuthImpl
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ULOGIN_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

  private val api by lazy { app().retrofit.create(Endpoint::class.java) }
  private val auth by lazy { KAuthImpl(applicationContext, api) as KAuth }
  private val users = mutableListOf<User>()
  private val adapter = UsersAdapter(users)
  private lateinit var loginOption: MenuItem
  private lateinit var currentTab: CurrentTab

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initNavigationView(findView(R.id.navigation))
    initRecyclerView(findView(R.id.recycler_view))
    initToolbar(findView(R.id.toolbar))
    api.latestUsers().enqueue(object : Callback<Map<String, User>> {
      override fun onResponse(call: Call<Map<String, User>>?, response: Response<Map<String, User>>?) {
        users.clear()
        response?.body()?.values?.forEach { user ->
          users.add(user)
          adapter.notifyItemInserted(0)
        }
      }

      override fun onFailure(call: Call<Map<String, User>>?, t: Throwable?) {
        log("cannot load users", t)
      }
    })
    api.latestBooksWithNotes().enqueue(object : Callback<Map<String, Book>> {
      override fun onResponse(call: Call<Map<String, Book>>?, response: Response<Map<String, Book>>?) {
        response?.body()?.values?.forEach { book ->
          logw(book.notes)
        }
      }

      override fun onFailure(call: Call<Map<String, Book>>?, t: Throwable?) {
        log("cannot load latest books with notes", t)
      }
    })
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
    currentTab = if (auth.isAuthorized()) DONE_TAB else HOME_TAB
    navigation.selectedItemId = currentTab.itemId
    navigation.setOnNavigationItemSelectedListener { item ->
      currentTab = CurrentTab.getByItemId(item.itemId)
      true
    }
  }

  private fun initRecyclerView(recyclerView: RecyclerView) {
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(this)
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
}
