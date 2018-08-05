package com.sirekanyan.knigopis.feature.notes

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.showProgressBar
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.feature.PagePresenter
import com.sirekanyan.knigopis.feature.PagesPresenter
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.model.NoteModel
import com.sirekanyan.knigopis.repository.NoteRepository

interface NotesPresenter : PagePresenter {

    interface Router {
        fun openUserScreen(note: NoteModel)
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
        router.openUserScreen(note)
    }

    override fun onNotesUpdated() {
        parent.onPageUpdated(CurrentTab.NOTES_TAB)
    }

}