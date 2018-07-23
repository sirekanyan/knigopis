package com.sirekanyan.knigopis.feature

import com.sirekanyan.knigopis.repository.Configuration

interface MainPresenter {

    interface Router {
        fun login()
        fun openProfileScreen()
        fun reopenScreen()
        fun openNewBookScreen()
    }

}

class MainPresenterImpl(
    private val view: MainView,
    private val router: MainPresenter.Router,
    private val config: Configuration
) : MainPresenter, MainView.Callbacks {

    override fun onLoginOptionClicked() {
        router.login()
    }

    override fun onProfileOptionClicked() {
        router.openProfileScreen()
    }

    override fun onAboutOptionClicked() {
        view.showAboutDialog()
    }

    override fun onDarkThemeOptionClicked(isChecked: Boolean) {
        config.isDarkTheme = isChecked
        router.reopenScreen()
    }

    override fun onAddBookClicked() {
        router.openNewBookScreen()
    }

}