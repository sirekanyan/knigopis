package com.sirekanyan.knigopis.feature.notes

import android.view.View
import com.sirekanyan.knigopis.common.android.adapter.CommonViewHolder
import com.sirekanyan.knigopis.common.extensions.setCircleImage
import com.sirekanyan.knigopis.common.extensions.setSquareImage
import com.sirekanyan.knigopis.model.NoteModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.note.*

class NoteViewHolder(
    override val containerView: View,
    private val onClick: (NoteModel) -> Unit
) : CommonViewHolder<NoteModel>(containerView),
    LayoutContainer {

    init {
        containerView.setOnClickListener {
            model?.let {
                onClick(it)
            }
        }
    }

    override fun onBind(position: Int, model: NoteModel) {
        bookImage.setSquareImage(model.bookImage)
        bookTitle.text = model.bookTitle
        bookAuthor.text = model.bookAuthor
        userNotes.text = model.noteContent
        userDate.text = model.noteDate
        userNickname.text = model.userName
        userImage.setCircleImage(model.userImage)
    }

}