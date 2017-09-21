package me.vadik.knigopis.adapters

import android.widget.TextView
import me.vadik.knigopis.R
import me.vadik.knigopis.model.User

object UsersAdapter {
  fun create(users: List<User>) = createAdapter<User, TextView>(
      users,
      R.layout.user,
      Adapter(R.id.user_name) { text = it.nickname },
      Adapter(R.id.book_count) {
        text = it.booksCount.toString()
        setTextColor(it.color)
      }
  )
}
