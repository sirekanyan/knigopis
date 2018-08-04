package com.sirekanyan.knigopis.feature.users

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.android.ResourceProvider
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.showProgressBar
import com.sirekanyan.knigopis.common.extensions.toUriOrNull
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.model.ProfileItem
import com.sirekanyan.knigopis.model.UserModel
import com.sirekanyan.knigopis.repository.UserRepository

interface UsersPresenter : Presenter {
    fun refresh()
    fun showUserProfiles(user: UserModel)
}

class UsersPresenterImpl(
    private val userRepository: UserRepository,
    private val resources: ResourceProvider
) : BasePresenter<UsersView>(),
    UsersPresenter {

    override fun refresh() {
        userRepository.observeUsers()
            .io2main()
            .showProgressBar(view)
            .bind({ users ->
                view.updateUsers(users)
            }, {
                logError("cannot load users", it)
                view.showUsersError(it)
            })
    }

    override fun showUserProfiles(user: UserModel) {
        val uriItems = user.profiles
            .mapNotNull(String::toUriOrNull)
            .map { ProfileItem(it, resources) }
            .distinctBy(ProfileItem::title)
        view.showUserProfiles(user.name, uriItems)
    }

}