package com.sirekanyan.knigopis.feature.profile

import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.extensions.toast
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.dto.User
import java.util.*

interface ProfilePresenter : Presenter {

    fun init()
    fun start()
    fun back(): Boolean

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
    private var user: User? = null

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

    override fun back(): Boolean =
        if (view.isEditMode) {
            view.quitEditMode()
            true
        } else {
            false
        }

    override fun onNavigationBackClicked() {
        router.exit()
    }

    override fun onEditOptionClicked() {
        view.enterEditMode()
    }

    override fun onSaveOptionClicked(nickname: String) {
        if (view.isNicknameChanged) {
            updateNickname(nickname)
        } else {
            view.quitEditMode()
        }
    }

    override fun onShareOptionClicked() {
        user?.fixedProfile?.let {
            router.shareProfile(it)
        }
    }

    override fun onLogoutOptionClicked() {
        interactor.logout()
        router.exit()
    }

    private fun refreshProfile() {
        interactor.getProfile()
            .bind({ user ->
                this.user = user
                view.setNickname(user.nickname.orEmpty())
                view.setAvatar(user.photo)
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

    private fun updateNickname(nickname: String) {
        val user = user ?: return
        interactor.updateProfile(user, nickname)
            .bind({
                view.setNickname(nickname)
                view.quitEditMode()
                refreshProfile()
            }, {
                view.toast(R.string.profile_error_save)
                logError("cannot update profile", it)
            })
    }

}