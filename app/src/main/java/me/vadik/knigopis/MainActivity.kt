package me.vadik.knigopis

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

  private val api by lazy { app().retrofit.create(Endpoint::class.java) }
  private val recyclerView by lazy { findViewById(R.id.recycler_view) as RecyclerView }
  private val users = mutableListOf<User>()

  private val onNavigationItemSelectedListener = { item: MenuItem ->
    supportActionBar!!.setTitle(when (item.itemId) {
      R.id.navigation_home -> R.string.title_home
      R.id.navigation_dashboard -> R.string.title_dashboard
      R.id.navigation_notifications -> R.string.title_notifications
      else -> throw UnsupportedOperationException()
    })
    true
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val navigation = findViewById(R.id.navigation) as BottomNavigationView
    navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    val adapter = UsersAdapter(users)
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(this)
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
}
