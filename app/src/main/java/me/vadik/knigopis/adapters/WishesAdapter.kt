package me.vadik.knigopis.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.vadik.knigopis.R
import me.vadik.knigopis.adapters.WishesAdapter.WishViewHolder
import me.vadik.knigopis.inflate
import me.vadik.knigopis.model.Wish

class WishesAdapter(private val wishes: List<Wish>) : RecyclerView.Adapter<WishViewHolder>() {

  class WishViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    val title: TextView = rootView.findViewById(R.id.book_title)
    val author: TextView = rootView.findViewById(R.id.book_author)
    val date: TextView = rootView.findViewById(R.id.book_read_date)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      WishViewHolder(parent.inflate(R.layout.book))

  override fun onBindViewHolder(holder: WishViewHolder, position: Int) {
    val wish = wishes[position]
    holder.title.text = wish.title
    holder.author.text = wish.author
    holder.date.text = wish.priority.toString()
  }

  override fun getItemCount() = wishes.size
}