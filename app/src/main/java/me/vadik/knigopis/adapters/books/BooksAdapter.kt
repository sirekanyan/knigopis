package me.vadik.knigopis.adapters.books

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.vadik.knigopis.R
import me.vadik.knigopis.createNewBookIntent
import me.vadik.knigopis.dialog.DialogFactory
import me.vadik.knigopis.dialog.createDialogItem
import me.vadik.knigopis.inflate

class BooksAdapter(
    private val books: List<UserBook>,
    private val dialogs: DialogFactory
) : RecyclerView.Adapter<BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookViewHolder(parent.inflate(R.layout.user_book))

    override fun getItemCount() =
        books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.title = book.title
        holder.author = book.author
        holder.notes = book.notes
        val context = holder.view.context
        holder.view.setOnLongClickListener {
            dialogs.showDialog(
                book.title + " — " + book.author,
                createDialogItem(R.string.add_book_todo, R.drawable.ic_playlist_add) {
                    context.startActivity(context.createNewBookIntent(book.title, book.author))
                },
                createDialogItem(R.string.add_book_done, R.drawable.ic_playlist_add_check) {
                    context.startActivity(context.createNewBookIntent(book.title, book.author, 100))
                }
            )
            true
        }
    }
}