package me.vadik.knigopis.adapters.users

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.note.view.*

class NoteViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    var nickname: String
        get() = view.userNickname.text.toString()
        set(value) {
            view.userNickname.text = value
        }

    var notes: String
        get() = view.userNotes.text.toString()
        set(value) {
            view.userNotes.text = value
        }
}