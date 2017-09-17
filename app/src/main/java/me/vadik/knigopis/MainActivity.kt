package me.vadik.knigopis

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

  private val api by lazy { app().retrofit.create(Endpoint::class.java) }

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
    api.latestUsers().enqueue(object : Callback<Map<String, User>> {
      override fun onResponse(call: Call<Map<String, User>>?, response: Response<Map<String, User>>?) {
        response?.body()?.forEach { (_, user) ->
          logw(user.nickname)
        }
      }

      override fun onFailure(call: Call<Map<String, User>>?, t: Throwable?) {
        log("cannot load users", t)
      }
    })
  }
}
