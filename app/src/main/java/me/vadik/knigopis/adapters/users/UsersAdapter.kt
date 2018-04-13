package me.vadik.knigopis.adapters.users

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.vadik.knigopis.R
import me.vadik.knigopis.Router
import me.vadik.knigopis.dialog.DialogFactory
import me.vadik.knigopis.dialog.DialogItem
import me.vadik.knigopis.inflate
import me.vadik.knigopis.model.subscription.Subscription

class UsersAdapter(
    private val users: List<Subscription>,
    private val router: Router,
    private val dialogs: DialogFactory
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
        holder.setAvatarUrl(user.subUser.avatar)
        holder.setNickname(user.subUser.name)
        holder.setBooksCount(user.subUser.booksCount)
        holder.setNewBooksCount(user.newBooksCount)
        holder.view.setOnClickListener {
            router.openUserScreen(user)
        }
        holder.view.setOnLongClickListener {
            dialogs.showDialog(
                user.subUser.name,
                DialogItem(R.string.user_option_delete, R.drawable.ic_delete) {
                    router.unsubscribe(user.subUser.id)
                }
            )
            true
        }
    }

}