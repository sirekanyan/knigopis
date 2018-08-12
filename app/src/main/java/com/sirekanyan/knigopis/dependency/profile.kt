package com.sirekanyan.knigopis.dependency

import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.getRootView
import com.sirekanyan.knigopis.feature.profile.ProfileActivity
import com.sirekanyan.knigopis.feature.profile.ProfilePresenter
import com.sirekanyan.knigopis.feature.profile.ProfilePresenterImpl
import com.sirekanyan.knigopis.feature.profile.ProfileViewImpl

fun ProfileActivity.providePresenter(): ProfilePresenter =
    ProfilePresenterImpl(
        this,
        app.endpoint,
        app.bookRepository,
        app.authRepository
    ).also { presenter ->
        presenter.view = ProfileViewImpl(getRootView(), presenter)
    }