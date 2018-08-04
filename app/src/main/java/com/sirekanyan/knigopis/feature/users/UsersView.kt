package com.sirekanyan.knigopis.feature.users

import com.sirekanyan.knigopis.model.ProfileItem
import com.sirekanyan.knigopis.model.UserModel

interface UsersView {

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