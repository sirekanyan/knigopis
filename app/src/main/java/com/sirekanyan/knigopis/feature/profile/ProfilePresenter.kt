package com.sirekanyan.knigopis.feature.profile

import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.extensions.toast
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.ProfileModel
import java.util.*

interface ProfilePresenter : Presenter {

    fun init()
    fun start()
    fun back()

    interface Router {
        fun shareProfile(profileUrl: String)
        fun exit()
    }

}

class ProfilePresenterImpl(
    private val router: ProfilePresenter.Router,
    private val interactor: ProfileInteractor
) : BasePresenter<ProfileView>(),
    ProfilePresenter,
    ProfileView.Callbacks {

    private val todoList = Stack<BookDataModel>()
    private val doingList = Stack<BookDataModel>()
    private val doneList = Stack<BookDataModel>()
    private var profile: ProfileModel? = null

    override fun init() {
        view.setTodoCount(0)
        view.setDoingCount(0)
        view.setDoneCount(0)
        view.setBooks(todoList, doingList, doneList)
    }

    override fun start() {
        refreshProfile()
        refreshCounters()
    }

    override fun back() {
        if (view.isEditMode) {
            view.quitEditMode()
        } else {
            router.exit()
        }
    }

    override fun onNavigationBackClicked() {
        back()
    }

    override fun onEditOptionClicked() {
        view.enterEditMode()
    }

    override fun onSaveOptionClicked(nickname: String) {
        val profile = profile?.copy(name = nickname) ?: return
        if (view.isProfileChanged) {
            updateProfile(profile)
        } else {
            view.quitEditMode()
        }
    }

    override fun onShareOptionClicked() {
        profile?.shareUrl?.let(router::shareProfile)
    }

    override fun onLogoutOptionClicked() {
        interactor.logout()
        router.exit()
    }

    private fun refreshProfile() {
        interactor.getProfile()
            .bind({ profile ->
                this.profile = profile
                view.setProfile(profile)
                view.setEditOptionVisible(true)
            }) {
                logError("cannot get profile", it)
            }
    }

    private fun refreshCounters() {
        interactor.getBooks()
            .doOnSubscribe {
                doneList.clear()
                doingList.clear()
                todoList.clear()
            }
            .bind({ book ->
                addBookToList(book)
            }, {
                logError("cannot get profile books", it)
            })
    }

    private fun addBookToList(book: BookDataModel) {
        when {
            book.isFinished -> {
                doneList.push(book)
                view.setDoneCount(doneList.size)
            }
            book.priority > 0 -> {
                doingList.push(book)
                view.setDoingCount(doingList.size)
            }
            else -> {
                todoList.push(book)
                view.setTodoCount(todoList.size)
            }
        }
    }

    private fun updateProfile(profile: ProfileModel) {
        interactor.updateProfile(profile)
            .bind({
                view.setProfile(profile)
                view.quitEditMode()
                refreshProfile()
            }, {
                view.toast(R.string.profile_error_save)
                logError("cannot update profile", it)
            })
    }

}