package com.sirekanyan.knigopis.feature.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.EditorInfo
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BaseActivity
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.common.functions.createTextShareIntent
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.dto.Profile
import com.sirekanyan.knigopis.model.dto.User
import com.sirekanyan.knigopis.repository.AuthRepository
import com.sirekanyan.knigopis.repository.BookRepository
import com.sirekanyan.knigopis.repository.Endpoint
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.profile_activity.*
import org.koin.android.ext.android.inject
import java.util.*
import java.util.concurrent.TimeUnit

fun Context.createProfileIntent() = Intent(this, ProfileActivity::class.java)

class ProfileActivity : BaseActivity() {

    private val api by inject<Endpoint>()
    private val bookRepository by inject<BookRepository>()
    private val auth by inject<AuthRepository>()
    private val todoList = Stack<BookDataModel>()
    private val doingList = Stack<BookDataModel>()
    private val doneList = Stack<BookDataModel>()
    private var userId: String? = null
    private var profileUrl: String? = null
    private lateinit var editOption: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)
        initToolbar(profileToolbar)
        profileTodoCount.text = getString(R.string.profile_text_todo, 0)
        profileDoingCount.text = getString(R.string.profile_text_doing, 0)
        profileDoneCount.text = getString(R.string.profile_text_done, 0)
        mapOf(
            profileTodoCount to todoList,
            profileDoingCount to doingList,
            profileDoneCount to doneList
        ).forEach { view, list ->
            view.setOnClickListener {
                if (!list.isEmpty()) {
                    showFooterBook(list.pop())
                }
            }
        }
        profileNicknameEditText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    updateNicknameOrExitEditMode()
                    true
                }
                else -> false
            }
        }
    }

    private fun showFooterBook(book: BookDataModel) {
        randomProfileBook.alpha = 1f
        randomProfileBook.text = getString(
            R.string.profile_text_random,
            resources.getTitleString(book.title),
            book.priority
        )
        randomProfileBook.animate()
            .setInterpolator(AccelerateInterpolator())
            .setDuration(1000)
            .alpha(0f)
    }

    override fun onStart() {
        super.onStart()
        refreshProfile()
        refreshCounters()
    }

    private fun refreshProfile() {
        api.getProfile(auth.getAccessToken()).io2main()
            .bind(::onRefreshProfile) {
                logError("cannot get profile", it)
            }
    }

    private fun onRefreshProfile(user: User) {
        userId = user.id
        profileUrl = user.fixedProfile
        profileNickname.text = user.nickname.orEmpty()
        profileAvatar.setCircleImage(user.photo)
        editOption.isVisible = true
    }

    private fun refreshCounters() {
        bookRepository.findCached()
            .toSingle(listOf())
            .map { it.filterIsInstance<BookDataModel>() }
            .map { it.shuffled() }
            .flatMapObservable {
                Observables.zip(
                    Observable.fromIterable(it),
                    Observable.interval(5, TimeUnit.MILLISECONDS)
                )
            }
            .io2main()
            .doOnSubscribe {
                doneList.clear()
                doingList.clear()
                todoList.clear()
            }
            .bind({ (book) ->
                addBookToList(book)
            }, {
                logError("cannot get cached books", it)
            })
    }

    @Suppress("USELESS_CAST")
    private fun addBookToList(book: BookDataModel) {
        when {
            book.isFinished -> {
                doneList.push(book)
                profileDoneCount.text =
                        getString(R.string.profile_text_done, doneList.size as Int)
            }
            book.priority > 0 -> {
                doingList.push(book)
                profileDoingCount.text =
                        getString(R.string.profile_text_doing, doingList.size as Int)
            }
            else -> {
                todoList.push(book)
                profileTodoCount.text =
                        getString(R.string.profile_text_todo, todoList.size as Int)
            }
        }
    }

    private fun updateNicknameOrExitEditMode() {
        if (profileNickname.text.toString() == profileNicknameEditText.text.toString()) {
            quitEditMode()
        } else {
            updateNickname()
        }
    }

    private fun updateNickname() {
        val id = userId ?: return
        api.updateProfile(
            id,
            auth.getAccessToken(),
            Profile(
                profileNicknameEditText.text.toString(),
                profileUrl.orEmpty()
            )
        ).io2main()
            .bind({
                profileNickname.text = profileNicknameEditText.text
                quitEditMode()
                refreshProfile()
            }, {
                toast(R.string.profile_error_save)
                logError("cannot update profile", it)
            })
    }

    private fun initToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.inflateMenu(R.menu.profile_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.option_edit_profile -> {
                    if (isEditMode) {
                        updateNicknameOrExitEditMode()
                    } else {
                        enterEditMode()
                        val nickname = profileNickname.text
                        profileNicknameEditText.setText(nickname)
                        profileNicknameEditText.setSelection(nickname.length, nickname.length)
                    }
                    true
                }
                R.id.option_share_profile -> {
                    profileUrl?.let {
                        startActivity(
                            createTextShareIntent(it, getString(R.string.profile_title_share))
                        )
                    }
                    true
                }
                R.id.option_logout_profile -> {
                    auth.logout()
                    finish()
                    true
                }
                else -> false
            }
        }
        editOption = toolbar.menu.findItem(R.id.option_edit_profile)
    }

    override fun onBackPressed() {
        if (profileNickname.isVisible) {
            super.onBackPressed()
        } else {
            quitEditMode()
        }
    }

    private fun enterEditMode() {
        editOption.setIcon(R.drawable.ic_done)
        editOption.setTitle(R.string.profile_option_save)
        topProfileSpace.hideNow()
        profileNicknameSwitcher.displayedChild = 1
        showKeyboard(profileNicknameEditText)
    }

    private fun quitEditMode() {
        editOption.setIcon(R.drawable.ic_edit)
        editOption.setTitle(R.string.profile_option_edit)
        hideKeyboard()
        topProfileSpace.showNow()
        profileNicknameSwitcher.displayedChild = 0
    }

    private val isEditMode
        get() = profileNicknameSwitcher.displayedChild == 1

}