package com.sirekanyan.knigopis.feature

import android.content.Context.MODE_PRIVATE
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import com.sirekanyan.knigopis.BuildConfig
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.extensions.hide
import com.sirekanyan.knigopis.common.extensions.show
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.model.CurrentTab.*
import com.sirekanyan.knigopis.repository.cache.COMMON_PREFS_NAME
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.about.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.books_page.*
import kotlinx.android.synthetic.main.default_app_bar.*
import kotlinx.android.synthetic.main.notes_page.*
import kotlinx.android.synthetic.main.users_page.*

interface MainView {

    fun showAboutDialog()
    fun showPage(tab: CurrentTab)
    fun showNavigation(isVisible: Boolean)
    fun setNavigation(itemId: Int)
    fun showLoginOption(isVisible: Boolean)
    fun showProfileOption(isVisible: Boolean)
    fun setDarkThemeOptionChecked(isChecked: Boolean)

    interface Callbacks {
        fun onNavigationClicked(itemId: Int)
        fun onToolbarClicked()
        fun onLoginOptionClicked()
        fun onProfileOptionClicked()
        fun onAboutOptionClicked()
        fun onDarkThemeOptionClicked(isChecked: Boolean)
    }

}

class MainViewImpl(
    override val containerView: View,
    private val callbacks: MainView.Callbacks
) : MainView, LayoutContainer {

    private val context = containerView.context
    private val loginOption: MenuItem
    private val profileOption: MenuItem
    private val darkThemeOption: MenuItem

    init {
        toolbar.inflateMenu(R.menu.options)
        toolbar.setOnClickListener {
            callbacks.onToolbarClicked()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.option_login -> {
                    callbacks.onLoginOptionClicked()
                    true
                }
                R.id.option_profile -> {
                    callbacks.onProfileOptionClicked()
                    true
                }
                R.id.option_about -> {
                    callbacks.onAboutOptionClicked()
                    true
                }
                R.id.option_dark_theme -> {
                    item.isChecked = !item.isChecked
                    callbacks.onDarkThemeOptionClicked(item.isChecked)
                    true
                }
                R.id.option_clear_cache -> {
                    context.cacheDir.deleteRecursively()
                    context.getSharedPreferences(COMMON_PREFS_NAME, MODE_PRIVATE)
                        .edit().clear().apply()
                    true
                }
                else -> false
            }
        }
        loginOption = toolbar.menu.findItem(R.id.option_login)
        profileOption = toolbar.menu.findItem(R.id.option_profile)
        darkThemeOption = toolbar.menu.findItem(R.id.option_dark_theme)
        toolbar.menu.findItem(R.id.option_clear_cache).isVisible = BuildConfig.DEBUG
    }

    override fun showAboutDialog() {
        val dialogView = View.inflate(context, R.layout.about, null)
        dialogView.aboutAppVersion.text = BuildConfig.VERSION_NAME
        AlertDialog.Builder(context).setView(dialogView).show()
    }

    override fun showPage(tab: CurrentTab) {
        if (tab == BOOKS_TAB) {
            addBookButton.translationX = 0f
            addBookButton.translationY = 0f
        }
        booksRecyclerView.stopScroll()
        usersRecyclerView.stopScroll()
        notesRecyclerView.stopScroll()
        booksPage.show(tab == BOOKS_TAB)
        usersPage.show(tab == USERS_TAB)
        notesPage.show(tab == NOTES_TAB)
    }

    override fun showNavigation(isVisible: Boolean) {
        if (isVisible) {
            bottomNavigation.show()
            bottomNavigation.setOnNavigationItemSelectedListener { item ->
                callbacks.onNavigationClicked(item.itemId)
                true
            }
        } else {
            bottomNavigation.hide()
            bottomNavigation.setOnNavigationItemSelectedListener(null)
        }
    }

    override fun setNavigation(itemId: Int) {
        bottomNavigation.menu.findItem(itemId).isChecked = true
    }

    override fun showLoginOption(isVisible: Boolean) {
        loginOption.isVisible = isVisible
    }

    override fun showProfileOption(isVisible: Boolean) {
        profileOption.isVisible = isVisible
    }

    override fun setDarkThemeOptionChecked(isChecked: Boolean) {
        darkThemeOption.isChecked = isChecked
    }

}