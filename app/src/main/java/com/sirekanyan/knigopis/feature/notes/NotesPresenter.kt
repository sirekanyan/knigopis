package com.sirekanyan.knigopis.feature.notes

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.showProgressBar
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.feature.PagesPresenter
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.model.NoteModel
import com.sirekanyan.knigopis.repository.NoteRepository

interface NotesPresenter : Presenter {

    fun refresh()

    interface Router {
        fun openUserScreen(id: String, name: String, image: String?)
    }

}

class NotesPresenterImpl(
    private val router: NotesPresenter.Router,
    private val noteRepository: NoteRepository
) : BasePresenter<NotesView>(),
    NotesPresenter,
    NotesView.Callbacks {

    lateinit var parent: PagesPresenter

    override fun refresh() {
        noteRepository.observeNotes()
            .io2main()
            .showProgressBar(view)
            .bind({ notes ->
                view.updateNotes(notes)
            }, {
                logError("cannot load notes", it)
                view.showNotesError(it)
            })
    }

    override fun onNoteClicked(note: NoteModel) {
        router.openUserScreen(note.userId, note.userName, note.userImage)
    }

    override fun onNotesUpdated() {
        parent.onPageUpdated(CurrentTab.NOTES_TAB)
    }

}