package com.sirekanyan.knigopis.feature.notes

import com.sirekanyan.knigopis.common.BasePresenter
import com.sirekanyan.knigopis.common.Presenter
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.showProgressBar
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.repository.NoteRepository

interface NotesPresenter : Presenter {
    fun refresh()
}

class NotesPresenterImpl(
    private val noteRepository: NoteRepository
) : BasePresenter<NotesView>(),
    NotesPresenter {

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

}