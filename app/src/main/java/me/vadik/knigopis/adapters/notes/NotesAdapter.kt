package me.vadik.knigopis.adapters.notes

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.vadik.knigopis.R
import me.vadik.knigopis.Router
import me.vadik.knigopis.inflate
import me.vadik.knigopis.model.note.Note

class NotesAdapter(
    private val notes: List<Note>,
    private val router: Router
) : RecyclerView.Adapter<NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = parent.inflate(R.layout.note)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.setTitle(note.title)
        holder.setAuthor(note.author)
        holder.setNotes(note.notes)
        holder.setTimestamp(note.fixedCreatedAt.time)
        holder.setNickname(note.user.nickname)
        holder.view.setOnClickListener {
            router.openUserScreen(note.user)
        }
    }

}