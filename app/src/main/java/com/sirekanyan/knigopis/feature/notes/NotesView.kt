package com.sirekanyan.knigopis.feature.notes

import com.sirekanyan.knigopis.model.NoteModel

interface NotesView {

    fun updateNotes(notes: List<NoteModel>)
    fun showNotesError(throwable: Throwable)

    interface Callbacks {
        fun onNoteClicked(note: NoteModel)
        fun onNotesUpdated()
    }

}