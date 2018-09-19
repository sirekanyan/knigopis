package com.sirekanyan.knigopis.feature

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.feature.login.LoginPresenter
import com.sirekanyan.knigopis.feature.users.MainPresenterState
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.model.CurrentTab.BOOKS_TAB
import com.sirekanyan.knigopis.model.CurrentTab.NOTES_TAB
import com.sirekanyan.knigopis.repository.AuthRepository
import com.sirekanyan.knigopis.repository.Configuration

interface MainPresenter : Presenter {

    val state: MainPresenterState?
    fun init(tab: CurrentTab?)
    fun start()
    fun resume()
    fun back(): Boolean
    fun onLoginScreenResult(token: String)
    fun onBookScreenResult()

    interface Router {
        fun openProfileScreen()
        fun reopenScreen()
    }

}

class MainPresenterImpl(
    private val loginPresenter: LoginPresenter,
    private val pagePresenters: Map<CurrentTab, PagePresenter>,
    private val router: MainPresenter.Router,
    private val config: Configuration,
    private val auth: AuthRepository
) : BasePresenter<MainView>(loginPresenter, *pagePresenters.values.toTypedArray()),
    MainPresenter,
    MainView.Callbacks,
    PagesPresenter,
    ProgressView.Callbacks {

    private val loadedTabs = mutableSetOf<CurrentTab>()
    private var currentTab: CurrentTab? = null
    private var booksChanged = false
    private var userLoggedIn = false

    override val state
        get() = currentTab?.let { MainPresenterState(it) }

    override fun init(tab: CurrentTab?) {
        view.setDarkThemeOptionChecked(config.isDarkTheme)
        val defaultTab = if (auth.isAuthorized()) BOOKS_TAB else NOTES_TAB
        this.currentTab = tab ?: defaultTab
    }

    override fun start() {
        refreshButtons()
        refresh()
    }

    override fun resume() {
        auth.authorize().bind({
            refreshButtons()
            if (userLoggedIn) {
                userLoggedIn = false
                currentTab = BOOKS_TAB
                refresh()
            }
        }, {
            logError("cannot check credentials", it)
        })
        if (booksChanged) {
            booksChanged = false
            refresh(isForce = true)
        }
    }

    override fun back(): Boolean =
        if (currentTab == BOOKS_TAB || !auth.isAuthorized()) {
            false
        } else {
            currentTab = BOOKS_TAB
            refresh()
            true
        }

    private fun refresh(isForce: Boolean = false) {
        if (!auth.isAuthorized()) {
            currentTab = NOTES_TAB
        }
        currentTab?.let {
            showPage(it, isForce)
            view.setNavigation(it.itemId)
        }
    }

    private fun refreshButtons() {
        auth.isAuthorized().let { authorized ->
            view.showLoginOption(!authorized)
            view.showProfileOption(authorized)
            view.showNavigation(authorized)
        }
    }

    private fun showPage(tab: CurrentTab, isForce: Boolean) {
        view.showPage(tab)
        val isFirst = !loadedTabs.contains(tab)
        if (isFirst || isForce) {
            pagePresenters[tab]?.refresh()
        }
    }

    override fun onLoginScreenResult(token: String) {
        auth.saveToken(token)
        userLoggedIn = true
    }

    override fun onBookScreenResult() {
        booksChanged = true
    }

    override fun onNavigationClicked(itemId: Int) {
        CurrentTab.getByItemId(itemId).let { tab ->
            currentTab = tab
            showPage(tab, false)
        }
    }

    override fun onToolbarClicked() {
        if (currentTab == BOOKS_TAB) {
            config.sortingMode = if (config.sortingMode == 0) 1 else 0
            refresh(isForce = true)
        }
    }

    override fun onLoginOptionClicked() {
        loginPresenter.login()
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

    override fun onRefreshSwiped() {
        refresh(isForce = true)
    }

    override fun onPageUpdated(tab: CurrentTab) {
        loadedTabs.add(tab)
    }

}