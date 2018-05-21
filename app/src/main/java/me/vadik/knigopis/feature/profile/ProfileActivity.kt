package me.vadik.knigopis.feature.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.profile_activity.*
import me.vadik.knigopis.*
import me.vadik.knigopis.common.*
import me.vadik.knigopis.repository.api.Endpoint
import me.vadik.knigopis.repository.KAuth
import me.vadik.knigopis.common.extensions.setCircleImage
import me.vadik.knigopis.repository.model.*
import me.vadik.knigopis.common.extensions.*
import org.koin.android.ext.android.inject

fun Context.createProfileIntent() = Intent(this, ProfileActivity::class.java)

class ProfileActivity : AppCompatActivity() {

    private val api by inject<Endpoint>()
    private val auth by inject<KAuth>()
    private val todoList = mutableListOf<Book>()
    private val doingList = mutableListOf<Book>()
    private val doneList = mutableListOf<Book>()
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
        profileTodoCount.setOnClickListener {
            setRandomFooterBook(todoList)
        }
        profileDoingCount.setOnClickListener {
            setRandomFooterBook(doingList)
        }
        profileDoneCount.setOnClickListener {
            setRandomFooterBook(doneList)
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

    private fun setRandomFooterBook(books: List<Book>) {
        val book = books.random() ?: return
        randomProfileBook.alpha = 1f
        randomProfileBook.text = getString(
            R.string.profile_text_random,
            book.titleOrDefault,
            (book as? PlannedBook)?.priority ?: 100
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
            .subscribe(::onRefreshProfile, {
                logError("cannot get profile", it)
            })
    }

    private fun onRefreshProfile(user: Credentials.UserFull) {
        userId = user.id
        profileUrl = user.fixedProfile
        profileNickname.text = user.nickname.orEmpty()
        profileAvatar.setCircleImage(user.photo)
        editOption.isVisible = true
    }

    private fun refreshCounters() {
        api.getFinishedBooks(auth.getAccessToken()).io2main()
            .subscribe(::onRefreshFinishedBooks, {
                logError("cannot check finished books count", it)
            })
        api.getPlannedBooks(auth.getAccessToken()).io2main()
            .subscribe(::onRefreshPlannedBooks, {
                logError("cannot check planned books count", it)
            })
    }

    @Suppress("USELESS_CAST")
    private fun onRefreshFinishedBooks(finishedBooks: List<FinishedBook>) {
        doneList.clearAndAddAll(finishedBooks)
        profileDoneCount.text = getString(R.string.profile_text_done, doneList.size as Int)
    }

    @Suppress("USELESS_CAST")
    private fun onRefreshPlannedBooks(plannedBooks: List<PlannedBook>) {
        doingList.clearAndAddAll(plannedBooks.filter { it.priority > 0 })
        profileDoingCount.text = getString(R.string.profile_text_doing, doingList.size as Int)
        todoList.clearAndAddAll(plannedBooks.filter { it.priority == 0 })
        profileTodoCount.text = getString(R.string.profile_text_todo, todoList.size as Int)
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
            Profile(profileNicknameEditText.text.toString(), profileUrl.orEmpty())
        ).io2main()
            .subscribe({
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
        showKeyboard()
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