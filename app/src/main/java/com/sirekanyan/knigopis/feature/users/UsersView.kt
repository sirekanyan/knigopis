package com.sirekanyan.knigopis.feature.users

import android.view.View
import com.sirekanyan.knigopis.common.android.dialog.DialogFactory
import com.sirekanyan.knigopis.common.android.dialog.DialogItem
import com.sirekanyan.knigopis.common.android.dialog.createDialogItem
import com.sirekanyan.knigopis.common.android.recycler.BottomOffsetItemDecoration
import com.sirekanyan.knigopis.common.extensions.hide
import com.sirekanyan.knigopis.common.extensions.keepOnTop
import com.sirekanyan.knigopis.common.extensions.show
import com.sirekanyan.knigopis.common.functions.handleError
import com.sirekanyan.knigopis.feature.ProgressView
import com.sirekanyan.knigopis.model.ProfileItem
import com.sirekanyan.knigopis.model.UserModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.users_page.*

interface UsersView : ProgressView {

    fun updateUsers(users: List<UserModel>)
    fun showUsersError(throwable: Throwable)
    fun showUserProfiles(title: String, items: List<ProfileItem>)

    interface Callbacks {
        fun onUserClicked(user: UserModel)
        fun onUserLongClicked(user: UserModel)
        fun onUserProfileClicked(uri: ProfileItem)
        fun onUsersUpdated()
    }

}

class UsersViewImpl(
    override val containerView: View,
    private val callbacks: UsersView.Callbacks,
    private val progressView: ProgressView,
    private val dialogs: DialogFactory
) : UsersView,
    LayoutContainer,
    ProgressView by progressView {

    private val usersAdapter = UsersAdapter(callbacks::onUserClicked, callbacks::onUserLongClicked)

    init {
        usersRecyclerView.adapter = usersAdapter
        usersRecyclerView.addItemDecoration(BottomOffsetItemDecoration(containerView.context))
    }

    override fun updateUsers(users: List<UserModel>) {
        usersPlaceholder.show(users.isEmpty())
        usersErrorPlaceholder.hide()
        usersAdapter.submitList(users) {
            usersRecyclerView.keepOnTop()
        }
        callbacks.onUsersUpdated()
    }

    override fun showUsersError(throwable: Throwable) {
        handleError(throwable, usersPlaceholder, usersErrorPlaceholder, usersAdapter)
    }

    override fun showUserProfiles(title: String, items: List<ProfileItem>) {
        val dialogItems: List<DialogItem> = items.map { uriItem ->
            createDialogItem(uriItem.title, uriItem.iconRes) {
                callbacks.onUserProfileClicked(uriItem)
            }
        }
        dialogs.showDialog(title, *dialogItems.toTypedArray())
    }

}