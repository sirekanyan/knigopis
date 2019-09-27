package com.sirekanyan.knigopis.dependency

import com.sirekanyan.knigopis.common.android.permissions.PermissionsImpl
import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.getRootView
import com.sirekanyan.knigopis.feature.login.LoginActivity
import com.sirekanyan.knigopis.feature.login.LoginPresenter
import com.sirekanyan.knigopis.feature.login.LoginPresenterImpl
import com.sirekanyan.knigopis.feature.login.LoginViewImpl

fun LoginActivity.providePresenter(): LoginPresenter {
    val permissions = PermissionsImpl(this, app.config)
    return LoginPresenterImpl(this, permissions).also { presenter ->
        presenter.view = LoginViewImpl(getRootView(), presenter)
        permissions.callback = presenter
    }
}