package com.sirekanyan.knigopis.dependency

import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.feature.user.UserActivity
import com.sirekanyan.knigopis.feature.user.UserInteractor
import com.sirekanyan.knigopis.feature.user.UserInteractorImpl

fun UserActivity.provideInteractor(): UserInteractor =
    UserInteractorImpl(app.authRepository, app.endpoint, app.resourceProvider)