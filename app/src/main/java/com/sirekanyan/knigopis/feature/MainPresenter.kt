package com.sirekanyan.knigopis.feature

import android.net.Uri
import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.android.ResourceProvider
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.toUriOrNull
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.feature.login.LoginPresenter
import com.sirekanyan.knigopis.feature.users.MainPresenterState
import com.sirekanyan.knigopis.model.*
import com.sirekanyan.knigopis.model.CurrentTab.*
import com.sirekanyan.knigopis.repository.*
import io.reactivex.Flowable

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
    private val router: MainPresenter.Router,
    private val config: Configuration,
    private val auth: AuthRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val noteRepository: NoteRepository,
    private val resources: ResourceProvider
) : BasePresenter<MainView>(loginPresenter),
    MainPresenter,
    MainView.Callbacks {

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
                HOME_TAB -> refreshHomeTab(tab)
                USERS_TAB -> refreshUsersTab(tab)
                NOTES_TAB -> refreshNotesTab(tab)
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
        view.showBookActions(book)
    }

    override fun onEditBookClicked(book: BookDataModel) {
        router.openBookScreen(book)
    }

    override fun onDeleteBookClicked(book: BookDataModel) {
        view.showBookDeleteDialog(book)
    }

    override fun onDeleteBookConfirmed(book: BookDataModel) {
        bookRepository.deleteBook(book)
            .io2main()
            .bind({
                refresh(isForce = true)
            }, {
                view.showBookDeleteError()
                logError("cannot delete finished book", it)
            })
    }

    override fun onUserClicked(user: UserModel) {
        router.openUserScreen(user.id, user.name, user.image)
    }

    override fun onUserLongClicked(user: UserModel) {
        val uriItems = user.profiles
            .mapNotNull(String::toUriOrNull)
            .map { ProfileItem(it, resources) }
            .distinctBy(ProfileItem::title)
        view.showUserProfiles(user.name, uriItems)
    }

    override fun onUserProfileClicked(uri: ProfileItem) {
        router.openWebPage(uri.uri)
    }

    override fun onNoteClicked(note: NoteModel) {
        router.openUserScreen(note.userId, note.userName, note.userImage)
    }

    private fun refreshHomeTab(tab: CurrentTab) {
        bookRepository.observeBooks()
            .io2main()
            .showProgressBar()
            .bind({ books ->
                view.updateBooks(books)
                loadedTabs.add(tab)
            }, {
                logError("cannot load books", it)
                view.showBooksError(it)
            })
    }

    private fun refreshUsersTab(tab: CurrentTab) {
        userRepository.observeUsers()
            .io2main()
            .showProgressBar()
            .bind({ users ->
                view.updateUsers(users)
                loadedTabs.add(tab)
            }, {
                logError("cannot load users", it)
                view.showUsersError(it)
            })
    }

    private fun refreshNotesTab(tab: CurrentTab) {
        noteRepository.observeNotes()
            .io2main()
            .showProgressBar()
            .bind({ notes ->
                view.updateNotes(notes)
                loadedTabs.add(tab)
            }, {
                logError("cannot load notes", it)
                view.showNotesError(it)
            })
    }

    private fun <T> Flowable<T>.showProgressBar(): Flowable<T> =
        doOnSubscribe {
            view.showProgress()
        }.doOnNext {
            view.hideProgress()
        }.doFinally {
            view.hideProgress()
            view.hideSwipeRefresh()
        }

}