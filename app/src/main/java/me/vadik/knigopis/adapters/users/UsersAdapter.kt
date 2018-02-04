package me.vadik.knigopis.adapters.users

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.vadik.knigopis.R
import me.vadik.knigopis.Router
import me.vadik.knigopis.inflate
import me.vadik.knigopis.model.subscription.Subscription

class UsersAdapter(
    private val users: List<Subscription>,
    private val router: Router
) : RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = parent.inflate(R.layout.user)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.nickname = user.subUser.nickname
        holder.avatarUrl = user.subUser.photo
        holder.profile = "${user.subUser.booksCount} прочитано" + user.newBooksCount?.let {
            " (+$it новых)"
        }.orEmpty()
        holder.view.setOnClickListener {
            router.openUserScreen(user)
        }
    }
}