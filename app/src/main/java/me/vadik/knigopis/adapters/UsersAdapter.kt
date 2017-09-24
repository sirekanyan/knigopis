package me.vadik.knigopis.adapters

import android.widget.TextView
import me.vadik.knigopis.R
import me.vadik.knigopis.model.User

object UsersAdapter {
  fun create(users: List<User>) = Adapter(users, { R.layout.user })
      .bind<TextView>(R.id.user_name) {
        text = it.nickname
      }
      .bind<TextView>(R.id.book_count) {
        text = it.booksCount.toString()
        setTextColor(it.color)
      }
      .build()
}
