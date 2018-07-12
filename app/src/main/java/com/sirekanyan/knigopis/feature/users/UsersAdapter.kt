package com.sirekanyan.knigopis.feature.users

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.Router
import com.sirekanyan.knigopis.common.ResourceProvider
import com.sirekanyan.knigopis.common.extensions.inflate
import com.sirekanyan.knigopis.common.view.dialog.DialogFactory
import com.sirekanyan.knigopis.common.view.dialog.createDialogItem
import com.sirekanyan.knigopis.repository.model.subscription.Subscription

class UsersAdapter(
    private val users: List<Subscription>,
    private val router: Router,
    private val dialogs: DialogFactory,
    private val resources: ResourceProvider
) : RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = parent.inflate(R.layout.user)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val subscription = users[position]
        val user = subscription.subUser
        holder.setAvatarUrl(user.avatar)
        holder.setNickname(user.name)
        holder.setBooksCount(user.booksCount)
        holder.setNewBooksCount(subscription.newBooksCount)
        holder.view.setOnClickListener {
            router.openUserScreen(user.id, user.name, user.avatar)
        }
        val dialogItems = user.profiles
            .map { UriItem(it, resources) }
            .distinctBy(UriItem::title)
            .map { uriItem ->
                createDialogItem(uriItem.title, uriItem.iconRes) {
                    router.openWebPage(uriItem.uri)
                }
            }
        holder.view.setOnLongClickListener {
            dialogs.showDialog(user.name, *dialogItems.toTypedArray())
            true
        }
    }

}