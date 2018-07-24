package com.sirekanyan.knigopis.feature.users

import com.sirekanyan.knigopis.model.UserModel

interface UsersView {

    fun updateUsers(users: List<UserModel>)
    fun showUsersError(throwable: Throwable)
    fun showUserProfiles(title: String, items: List<UriItem>)

    interface Callbacks {
        fun onUserClicked(user: UserModel)
        fun onUserLongClicked(user: UserModel)
        fun onUserProfileClicked(uri: UriItem)
    }

}