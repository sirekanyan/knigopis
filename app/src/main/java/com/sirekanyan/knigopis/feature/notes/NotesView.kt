package com.sirekanyan.knigopis.feature.notes

import android.view.View
import com.sirekanyan.knigopis.common.extensions.hide
import com.sirekanyan.knigopis.common.extensions.show
import com.sirekanyan.knigopis.common.functions.handleError
import com.sirekanyan.knigopis.feature.ProgressView
import com.sirekanyan.knigopis.model.NoteModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.notes_page.*

interface NotesView : ProgressView {

    fun updateNotes(notes: List<NoteModel>)
    fun showNotesError(throwable: Throwable)

    interface Callbacks {
        fun onNoteClicked(note: NoteModel)
        fun onNotesUpdated()
    }

}

class NotesViewImpl(
    override val containerView: View,
    private val callbacks: NotesView.Callbacks,
    private val progressView: ProgressView
) : NotesView,
    LayoutContainer,
    ProgressView by progressView {

    private val notesAdapter = NotesAdapter(callbacks::onNoteClicked)

    init {
        notesRecyclerView.adapter = notesAdapter
    }

    override fun updateNotes(notes: List<NoteModel>) {
        notesPlaceholder.show(notes.isEmpty())
        notesErrorPlaceholder.hide()
        notesAdapter.submitList(notes)
        callbacks.onNotesUpdated()
    }

    override fun showNotesError(throwable: Throwable) {
        handleError(throwable, notesPlaceholder, notesErrorPlaceholder, notesAdapter)
    }

}