package com.sirekanyan.knigopis.dependency

import com.sirekanyan.knigopis.common.android.dialog.DialogFactory
import com.sirekanyan.knigopis.common.android.permissions.PermissionsImpl
import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.getRootView
import com.sirekanyan.knigopis.feature.*
import com.sirekanyan.knigopis.feature.books.BooksPresenterImpl
import com.sirekanyan.knigopis.feature.books.BooksViewImpl
import com.sirekanyan.knigopis.feature.notes.NotesPresenterImpl
import com.sirekanyan.knigopis.feature.notes.NotesViewImpl
import com.sirekanyan.knigopis.feature.users.UsersPresenterImpl
import com.sirekanyan.knigopis.feature.users.UsersViewImpl
import com.sirekanyan.knigopis.model.CurrentTab.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.books_page.view.*
import kotlinx.android.synthetic.main.notes_page.view.*
import kotlinx.android.synthetic.main.users_page.view.*

fun MainActivity.providePresenter(): MainPresenter {
    val booksPresenter = BooksPresenterImpl(this, app.bookRepository)
    val usersPresenter = UsersPresenterImpl(this, app.userRepository, app.resourceProvider)
    val notesPresenter = NotesPresenterImpl(this, app.noteRepository)
    val permissions = PermissionsImpl(this, app.config)
    return MainPresenterImpl(
        mapOf(
            BOOKS_TAB to booksPresenter,
            USERS_TAB to usersPresenter,
            NOTES_TAB to notesPresenter
        ),
        this,
        app.config,
        app.authRepository,
        permissions
    ).also { mainPresenter ->
        val rootView = getRootView()
        val progressView = ProgressViewImpl(rootView.swipeRefresh, mainPresenter)
        val dialogs: DialogFactory = provideDialogs()
        booksPresenter.also { p ->
            p.view = BooksViewImpl(rootView.booksPage, booksPresenter, progressView, dialogs)
            p.parent = mainPresenter
        }
        usersPresenter.also { p ->
            p.view = UsersViewImpl(rootView.usersPage, usersPresenter, progressView, dialogs)
            p.parent = mainPresenter
        }
        notesPresenter.also { p ->
            p.view = NotesViewImpl(rootView.notesPage, notesPresenter, progressView)
            p.parent = mainPresenter
        }
        mainPresenter.view = MainViewImpl(rootView, mainPresenter)
        permissions.callback = mainPresenter
    }
}