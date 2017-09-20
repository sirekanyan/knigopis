package me.vadik.knigopis.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.vadik.knigopis.R
import me.vadik.knigopis.adapters.UsersAdapter.UserViewHolder
import me.vadik.knigopis.inflate
import me.vadik.knigopis.model.User

class UsersAdapter(private val users: List<User>) : RecyclerView.Adapter<UserViewHolder>() {

  class UserViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    val userName: TextView = rootView.findViewById(R.id.user_name)
    val bookCount: TextView = rootView.findViewById(R.id.book_count)
  }

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
