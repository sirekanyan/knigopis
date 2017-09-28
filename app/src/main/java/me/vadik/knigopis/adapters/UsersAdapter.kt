package me.vadik.knigopis.adapters

import android.widget.TextView
import me.vadik.knigopis.R
import me.vadik.knigopis.model.User

object UsersAdapter {
  fun create(users: List<User>) = Adapter(users, { R.layout.user })
      .bind<TextView>(R.id.user_name) {
        text = users[it].nickname
      }
      .bind<TextView>(R.id.book_count) {
        val user = users[it]
        text = user.booksCount.toString()
        setTextColor(user.color)
      }
      .get()
}
