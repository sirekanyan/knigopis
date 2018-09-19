package com.sirekanyan.knigopis.dependency

import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.getRootView
import com.sirekanyan.knigopis.feature.user.*

fun UserActivity.providePresenter(id: String, name: String, image: String?): UserPresenter {
    val interactor = UserInteractorImpl(app.endpoint, app.resourceProvider)
    return UserPresenterImpl(this, interactor, id, name, image, app.resourceProvider)
        .also { it.view = UserViewImpl(getRootView(), it, provideDialogs()) }
}