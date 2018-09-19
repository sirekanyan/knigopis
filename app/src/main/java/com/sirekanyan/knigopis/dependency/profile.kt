package com.sirekanyan.knigopis.dependency

import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.getRootView
import com.sirekanyan.knigopis.feature.profile.*

fun ProfileActivity.providePresenter(): ProfilePresenter {
    val interactor = ProfileInteractorImpl(app.endpoint, app.bookRepository, app.tokenStorage)
    return ProfilePresenterImpl(this, interactor).also { presenter ->
        presenter.view = ProfileViewImpl(getRootView(), presenter)
    }
}