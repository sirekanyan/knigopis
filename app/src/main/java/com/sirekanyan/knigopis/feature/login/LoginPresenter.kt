package com.sirekanyan.knigopis.feature.login

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.android.permissions.Permission
import com.sirekanyan.knigopis.common.android.permissions.PermissionResult
import com.sirekanyan.knigopis.common.android.permissions.Permissions

interface LoginPresenter : Presenter {

    fun init()
    fun onPermissionResult(permissionResult: PermissionResult)

    interface Router {

        fun openBrowser(website: Website): Boolean
        fun openMarket(packageName: String)
        fun openLegacyLoginScreen()
        fun close()

    }

}

class LoginPresenterImpl(
    private val router: LoginPresenter.Router,
    private val permissions: Permissions
) : BasePresenter<LoginView>(),
    LoginPresenter,
    LoginView.Callbacks,
    Permissions.Callback {

    override fun init() {
        Website.values().forEach { website ->
            view.addWebsite(website)
        }
    }

    override fun onWebsiteClicked(website: Website) {
        if (!router.openBrowser(website)) {
            view.showNoBrowserDialog()
        }
    }

    override fun onInstallBrowserClicked(packageName: String) {
        router.openMarket(packageName)
    }

    override fun onLegacyLoginClicked() {
        permissions.requestPermission(Permission.PHONE)
    }

    override fun onPermissionResult(permissionResult: PermissionResult) {
        permissions.submitResult(permissionResult)
    }

    override fun onGranted(permission: Permission) {
        if (permission == Permission.PHONE) {
            router.openLegacyLoginScreen()
        }
    }

    override fun onBackClicked() {
        router.close()
    }

}