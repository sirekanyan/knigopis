package com.sirekanyan.knigopis.feature.notes

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import com.sirekanyan.knigopis.common.extensions.setCircleImage
import com.sirekanyan.knigopis.common.extensions.setSquareImage
import kotlinx.android.synthetic.main.note.view.*

class NoteViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun setTitle(title: String) {
        view.bookTitle.text = title
    }

    fun setAuthor(author: String) {
        view.bookAuthor.text = author
    }

    fun setNotes(notes: String) {
        view.userNotes.text = notes
    }

    fun setTimestamp(timestamp: Long) {
        view.userDate.text = DateUtils.getRelativeTimeSpanString(timestamp)
    }

    fun setNickname(nickname: String) {
        view.userNickname.text = nickname
    }

    fun setAvatarUrl(url: String?) {
        view.userSmallAvatar.setCircleImage(url)
    }

    fun setBookImageUrl(url: String?) {
        view.bookImage.setSquareImage(url)
    }

}