package me.vadik.knigopis

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.vadik.knigopis.model.User

class UsersAdapter(private val users: List<User>) : RecyclerView.Adapter<UserViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      UserViewHolder(parent.inflate(R.layout.user))

  override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
    val user = users[position]
    holder.userName.text = user.nickname
    holder.bookCount.text = user.booksCount.toString()
    holder.bookCount.setTextColor(user.color)
  }

  override fun getItemCount() = users.size
}
