package com.sirekanyan.knigopis.feature

import android.net.Uri
import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.ResourceProvider
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.toUriOrNull
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.feature.users.UriItem
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.model.CurrentTab.*
import com.sirekanyan.knigopis.model.NoteModel
import com.sirekanyan.knigopis.model.UserModel
import com.sirekanyan.knigopis.repository.*
import io.reactivex.Flowable

interface MainPresenter : Presenter {

    var currentTab: CurrentTab
    fun refresh(tab: CurrentTab? = null, isForce: Boolean = false)
    fun showPage(tab: CurrentTab, isForce: Boolean)

    interface Router {
        fun login()
        fun openProfileScreen()
        fun reopenScreen()
        fun openNewBookScreen()
        fun openBookScreen(book: BookDataModel)
        fun openUserScreen(id: String, name: String, image: String?)
        fun openWebPage(uri: Uri)
    }
}

class MainPresenterImpl(
    private val router: MainPresenter.Router,
    private val config: Configuration,
    private val auth: KAuth,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val noteRepository: NoteRepository,
    private val resources: ResourceProvider
) : BasePresenter<MainView>(), MainPresenter, MainView.Callbacks {

    override lateinit var currentTab: CurrentTab
    private val loadedTabs = mutableSetOf<CurrentTab>()

    override fun refresh(tab: CurrentTab?, isForce: Boolean) {
        if (!auth.isAuthorized()) {
            currentTab = NOTES_TAB
        } else if (tab != null) {
            currentTab = tab
        }
        showPage(currentTab, isForce)
        view.setNavigation(currentTab.itemId)
    }

    override fun showPage(tab: CurrentTab, isForce: Boolean) {
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
            .map { UriItem(it, resources) }
            .distinctBy(UriItem::title)
        view.showUserProfiles(user.name, uriItems)
    }

    override fun onUserProfileClicked(uri: UriItem) {
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