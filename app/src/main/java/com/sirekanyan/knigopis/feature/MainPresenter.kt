package com.sirekanyan.knigopis.feature

import android.net.Uri
import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.feature.books.BooksPresenter
import com.sirekanyan.knigopis.feature.books.BooksView
import com.sirekanyan.knigopis.feature.login.LoginPresenter
import com.sirekanyan.knigopis.feature.notes.NotesPresenter
import com.sirekanyan.knigopis.feature.notes.NotesView
import com.sirekanyan.knigopis.feature.users.MainPresenterState
import com.sirekanyan.knigopis.feature.users.UsersPresenter
import com.sirekanyan.knigopis.feature.users.UsersView
import com.sirekanyan.knigopis.model.*
import com.sirekanyan.knigopis.model.CurrentTab.*
import com.sirekanyan.knigopis.repository.AuthRepository
import com.sirekanyan.knigopis.repository.Configuration

interface MainPresenter : Presenter {

    val state: MainPresenterState?
    fun init(state: MainPresenterState?)
    fun start()
    fun resume()
    fun back(): Boolean
    fun onLoginScreenResult(token: String)
    fun onBookScreenResult()

    interface Router {
        fun openProfileScreen()
        fun openNewBookScreen()
        fun openBookScreen(book: BookDataModel)
        fun openUserScreen(id: String, name: String, image: String?)
        fun openWebPage(uri: Uri)
        fun reopenScreen()
    }

}

class MainPresenterImpl(
    private val loginPresenter: LoginPresenter,
    private val booksPresenter: BooksPresenter,
    private val usersPresenter: UsersPresenter,
    private val notesPresenter: NotesPresenter,
    private val router: MainPresenter.Router,
    private val config: Configuration,
    private val auth: AuthRepository
) : BasePresenter<MainView>(loginPresenter, booksPresenter, usersPresenter, notesPresenter),
    MainPresenter,
    MainView.Callbacks,
    BooksView.Callbacks,
    UsersView.Callbacks,
    NotesView.Callbacks,
    ProgressView.Callbacks {

    private val loadedTabs = mutableSetOf<CurrentTab>()
    private var currentTab: CurrentTab? = null
    private var booksChanged = false
    private var userLoggedIn = false

    override val state
        get() = currentTab?.let { MainPresenterState(it) }

    override fun init(state: MainPresenterState?) {
        view.setDarkThemeOptionChecked(config.isDarkTheme)
        val defaultTab = if (auth.isAuthorized()) HOME_TAB else NOTES_TAB
        this.currentTab = state?.currentTab ?: defaultTab
    }

    override fun start() {
        refreshButtons()
        refresh(currentTab)
    }

    override fun resume() {
        auth.loadAccessToken().bind({
            refreshButtons()
            if (userLoggedIn) {
                userLoggedIn = false
                refresh(HOME_TAB)
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
        if (currentTab == HOME_TAB || !auth.isAuthorized()) {
            false
        } else {
            refresh(HOME_TAB)
            true
        }

    private fun refresh(tab: CurrentTab? = null, isForce: Boolean = false) {
        if (!auth.isAuthorized()) {
            currentTab = NOTES_TAB
        } else if (tab != null) {
            currentTab = tab
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
            when (tab) {
                HOME_TAB -> booksPresenter.refresh()
                USERS_TAB -> usersPresenter.refresh()
                NOTES_TAB -> notesPresenter.refresh()
            }
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
        if (currentTab == HOME_TAB) {
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

    override fun onAddBookClicked() {
        router.openNewBookScreen()
    }

    override fun onRefreshSwiped() {
        refresh(isForce = true)
    }

    override fun onBookClicked(book: BookDataModel) {
        router.openBookScreen(book)
    }

    override fun onBookLongClicked(book: BookDataModel) {
        booksPresenter.showBookActions(book)
    }

    override fun onEditBookClicked(book: BookDataModel) {
        router.openBookScreen(book)
    }

    override fun onDeleteBookClicked(book: BookDataModel) {
        booksPresenter.showBookDeleteDialog(book)
    }

    override fun onDeleteBookConfirmed(book: BookDataModel) {
        booksPresenter.deleteBook(book)
    }

    override fun onUserClicked(user: UserModel) {
        router.openUserScreen(user.id, user.name, user.image)
    }

    override fun onUserLongClicked(user: UserModel) {
        usersPresenter.showUserProfiles(user)
    }

    override fun onUserProfileClicked(uri: ProfileItem) {
        router.openWebPage(uri.uri)
    }

    override fun onNoteClicked(note: NoteModel) {
        router.openUserScreen(note.userId, note.userName, note.userImage)
    }

    override fun onBooksUpdated() {
        loadedTabs.add(HOME_TAB)
    }

    override fun onUsersUpdated() {
        loadedTabs.add(USERS_TAB)
    }

    override fun onNotesUpdated() {
        loadedTabs.add(NOTES_TAB)
    }

}