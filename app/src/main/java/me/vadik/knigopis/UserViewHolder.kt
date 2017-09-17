package me.vadik.knigopis

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.widget.TextView

class UserViewHolder(rootView: View) : ViewHolder(rootView) {
  val userName: TextView = rootView.findViewById(R.id.user_name)
  val bookCount: TextView = rootView.findViewById(R.id.book_count)
}