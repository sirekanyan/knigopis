package com.sirekanyan.knigopis.feature.login

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.feature.login.LoginPresenter.Router

interface LoginPresenter : Presenter {

    fun init()

    interface Router {

        fun openBrowser(website: Website): Boolean
        fun openMarket(packageName: String)
        fun close()

    }

}

class LoginPresenterImpl(private val router: Router) : BasePresenter<LoginView>(),
    LoginPresenter,
    LoginView.Callbacks {

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

    override fun onBackClicked() {
        router.close()
    }

}