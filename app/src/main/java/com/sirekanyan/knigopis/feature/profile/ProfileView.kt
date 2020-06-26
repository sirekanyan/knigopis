package com.sirekanyan.knigopis.feature.profile

import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.Toolbar
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.toast.CommonView
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.ProfileModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.profile_activity.*
import java.util.*

interface ProfileView : CommonView {

    val isEditMode: Boolean
    val isProfileChanged: Boolean
    fun setProfile(profile: ProfileModel)
    fun setEditOptionVisible(isVisible: Boolean)
    fun setTodoCount(count: Int)
    fun setDoingCount(count: Int)
    fun setDoneCount(count: Int)
    fun enterEditMode()
    fun quitEditMode()
    fun setBooks(
        todo: Stack<BookDataModel>,
        doing: Stack<BookDataModel>,
        done: Stack<BookDataModel>
    )

    interface Callbacks {
        fun onNavigationBackClicked()
        fun onEditOptionClicked()
        fun onSaveOptionClicked(nickname: String)
        fun onShareOptionClicked()
        fun onLogoutOptionClicked()
    }

}

class ProfileViewImpl(
    override val containerView: View,
    private val callbacks: ProfileView.Callbacks
) : ProfileView,
    LayoutContainer {

    private lateinit var editOption: MenuItem
    override val isEditMode get() = profileNicknameSwitcher.displayedChild == 1
    override val isProfileChanged get() = profileNickname.text.toString() != profileNicknameEditText.text.toString()

    init {
        initToolbar(profileToolbar)
        profileNicknameEditText.setOnEditorActionListener { view, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    callbacks.onSaveOptionClicked(view.text.toString())
                    true
                }
                else -> false
            }
        }
    }

    override fun setProfile(profile: ProfileModel) {
        profileNickname.text = profile.name
        profileAvatar.setCircleImageOnPrimary(profile.imageUrl)
    }

    override fun setEditOptionVisible(isVisible: Boolean) {
        editOption.isVisible = isVisible
    }

    override fun setTodoCount(count: Int) {
        profileTodoCount.text = context.getString(R.string.profile_text_todo, count)
    }

    override fun setDoingCount(count: Int) {
        profileDoingCount.text = context.getString(R.string.profile_text_doing, count)
    }

    override fun setDoneCount(count: Int) {
        profileDoneCount.text = context.getString(R.string.profile_text_done, count)
    }

    override fun enterEditMode() {
        editOption.setIcon(R.drawable.ic_done)
        editOption.setTitle(R.string.profile_option_save)
        topProfileSpace.hideNow()
        profileNicknameSwitcher.displayedChild = 1
        containerView.showKeyboard(profileNicknameEditText)
        run {
            val nickname = profileNickname.text
            profileNicknameEditText.setText(nickname)
            profileNicknameEditText.setSelection(nickname.length, nickname.length)
        }
    }

    override fun quitEditMode() {
        editOption.setIcon(R.drawable.ic_edit)
        editOption.setTitle(R.string.profile_option_edit)
        containerView.hideKeyboard()
        topProfileSpace.showNow()
        profileNicknameSwitcher.displayedChild = 0
    }

    override fun setBooks(
        todo: Stack<BookDataModel>,
        doing: Stack<BookDataModel>,
        done: Stack<BookDataModel>
    ) {
        mapOf(
            profileTodoCount to todo,
            profileDoingCount to doing,
            profileDoneCount to done
        ).forEach { (view, list) ->
            view.setOnClickListener {
                if (!list.isEmpty()) {
                    showFooterBook(list.pop())
                }
            }
        }
    }

    private fun initToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { callbacks.onNavigationBackClicked() }
        toolbar.inflateMenu(R.menu.profile_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.option_edit_profile -> {
                    if (isEditMode) {
                        callbacks.onSaveOptionClicked(profileNicknameEditText.text.toString())
                    } else {
                        callbacks.onEditOptionClicked()
                    }
                    true
                }
                R.id.option_share_profile -> {
                    callbacks.onShareOptionClicked()
                    true
                }
                R.id.option_logout_profile -> {
                    callbacks.onLogoutOptionClicked()
                    true
                }
                else -> false
            }
        }
        editOption = toolbar.menu.findItem(R.id.option_edit_profile)
    }

    private fun showFooterBook(book: BookDataModel) {
        randomProfileBook.alpha = 1f
        randomProfileBook.text = context.getString(
            R.string.profile_text_random,
            resources.getTitleString(book.title),
            book.priority
        )
        randomProfileBook.animate()
            .setInterpolator(AccelerateInterpolator())
            .setDuration(1000)
            .alpha(0f)
    }

}