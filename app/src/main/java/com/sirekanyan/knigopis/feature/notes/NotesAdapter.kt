package com.sirekanyan.knigopis.feature.notes

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.ViewGroup
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.adapter.SimpleItemCallback
import com.sirekanyan.knigopis.common.extensions.inflate
import com.sirekanyan.knigopis.model.NoteModel

class NotesAdapter(
    private val onClick: (NoteModel) -> Unit
) : ListAdapter<NoteModel, NoteViewHolder>(SimpleItemCallback(NoteModel::id)) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteViewHolder(parent.inflate(R.layout.note), onClick)

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(position, getItem(position))
    }

}