package me.vadik.knigopis

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import me.vadik.knigopis.CurrentTab.*
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

  private val api by lazy { app().retrofit.create(Endpoint::class.java) }
  private val users = mutableListOf<User>()
  private val adapter = UsersAdapter(users)
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

  private fun initNavigationView(navigation: BottomNavigationView) {
    currentTab = HOME_TAB
    navigation.setOnNavigationItemSelectedListener { item ->
      currentTab = CurrentTab.getByItemId(item.itemId)
      true
    }
  }

  private fun initRecyclerView(recyclerView: RecyclerView) {
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
  }

  private fun initToolbar(toolbar: Toolbar) {
    toolbar.inflateMenu(R.menu.options)
  }
}
