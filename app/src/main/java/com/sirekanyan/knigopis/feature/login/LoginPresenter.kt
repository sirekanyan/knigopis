package com.sirekanyan.knigopis.feature.login

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.android.Permissions
import com.sirekanyan.knigopis.common.functions.logError

interface LoginPresenter : Presenter {

    fun login()

    interface Router {
        fun openLoginScreen()
        fun openSettingsScreen()
    }

}

class LoginPresenterImpl(
    private val router: LoginPresenter.Router,
    private val permissions: Permissions
) : BasePresenter<LoginView>(),
    LoginPresenter,
    LoginView.Callbacks {

    override fun login() {
        permissions.requestReadPhoneState().bind({
            when {
                it.granted -> {
                    router.openLoginScreen()
                }
                it.shouldShowRequestPermissionRationale -> {
                    view.showPermissionsRetryDialog()
                }
                else -> {
                    view.showPermissionsSettingsDialog()
                }
            }
        }, {
            logError("cannot request permission", it)
        })
    }

    override fun onRetryLoginClicked() {
        login()
    }

    override fun onGotoSettingsClicked() {
        router.openSettingsScreen()
    }

}