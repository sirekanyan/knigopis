package me.vadik.knigopis

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

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
  }
}
