package com.sirekanyan.knigopis.feature.user

import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.dialog.DialogFactory
import com.sirekanyan.knigopis.common.android.dialog.createDialogItem
import com.sirekanyan.knigopis.common.android.header.HeaderItemDecoration
import com.sirekanyan.knigopis.common.android.header.StickyHeaderImpl
import com.sirekanyan.knigopis.common.android.toast.CommonView
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_activity.*

interface UserView : CommonView {

    fun setTitle(title: String)
    fun setImage(url: String?)
    fun setBooks(books: List<BookModel>)
    fun showUnsubscribeOption()
    fun showProgress()
    fun hideProgress()
    fun showBooks()
    fun showError()
    fun showFab(isVisible: Boolean)
    fun disableFabClick()
    fun setFabSelected(isSelected: Boolean)
    fun showActionsDialog(title: String, author: String, book: BookDataModel)

    interface Callbacks {
        fun onNavigationBackClicked()
        fun onCopyOptionClicked()
        fun onUnsubscribeOptionClicked()
        fun onFabClicked()
        fun onBookLongClicked(book: BookDataModel)
        fun onTodoActionClicked(book: BookDataModel)
        fun onDoneActionClicked(book: BookDataModel)
    }

}

class UserViewImpl(
    override val containerView: View,
    private val callbacks: UserView.Callbacks,
    private val dialogs: DialogFactory
) : UserView,
    LayoutContainer {

    private val context = containerView.context
    private val unsubscribeOption: MenuItem
    private val booksAdapter = UserBooksAdapter(callbacks::onBookLongClicked)

    init {
        initToolbar(callbacks)
        unsubscribeOption = toolbar.menu.findItem(R.id.option_unsubscribe)
        fab.setOnClickListener { callbacks.onFabClicked() }
        val layoutManager = LinearLayoutManager(context)
        userBooksRecyclerView.layoutManager = layoutManager
        userBooksRecyclerView.addItemDecoration(HeaderItemDecoration(StickyHeaderImpl(booksAdapter)))
        userBooksRecyclerView.adapter = booksAdapter
    }

    override fun setTitle(title: String) {
        toolbar.title = title
    }

    override fun setImage(url: String?) {
        userImage.setCircleImage(url, R.drawable.oval_dark_placeholder_background)
        userImage.setElevationRes(R.dimen.image_view_elevation)
    }

    override fun setBooks(books: List<BookModel>) {
        booksAdapter.submitList(books)
    }

    override fun showUnsubscribeOption() {
        unsubscribeOption.isVisible = true
    }

    override fun showProgress() {
        userBooksProgressBar.showNow()
        userBooksRecyclerView.hideNow()
        userBooksErrorPlaceholder.hideNow()
    }

    override fun hideProgress() {
        userBooksProgressBar.hide()
    }

    override fun showBooks() {
        userBooksRecyclerView.show()
    }

    override fun showError() {
        userBooksErrorPlaceholder.show()
    }

    override fun showFab(isVisible: Boolean) {
        if (isVisible) {
            fab.showNow()
            fab.startExpandAnimation()
        } else {
            fab.startCollapseAnimation()
        }
    }

    override fun disableFabClick() {
        fab.setOnClickListener(null)
    }

    override fun setFabSelected(isSelected: Boolean) {
        fab.isSelected = isSelected
        fab.setImageResource(
            if (isSelected) {
                R.drawable.ic_done
            } else {
                R.drawable.ic_person_add
            }
        )
    }

    override fun showActionsDialog(title: String, author: String, book: BookDataModel) {
        dialogs.showDialog(
            context.resources.getFullTitleString(book.title, book.author),
            createDialogItem(R.string.user_button_todo, R.drawable.ic_playlist_add) {
                callbacks.onTodoActionClicked(book)
            },
            createDialogItem(R.string.user_button_done, R.drawable.ic_playlist_add_check) {
                callbacks.onDoneActionClicked(book)
            }
        )
    }

    private fun initToolbar(callbacks: UserView.Callbacks) {
        toolbar.inflateMenu(R.menu.user_menu)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { callbacks.onNavigationBackClicked() }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.option_copy -> {
                    callbacks.onCopyOptionClicked()
                    true
                }
                R.id.option_unsubscribe -> {
                    callbacks.onUnsubscribeOptionClicked()
                    true
                }
                else -> false
            }
        }
    }

}