package me.vadik.knigopis.adapters.books

import me.vadik.knigopis.R
import me.vadik.knigopis.common.adapter.AbstractBooksAdapter
import me.vadik.knigopis.common.adapter.BookHeaderViewHolder
import me.vadik.knigopis.common.adapter.BookItemViewHolder
import me.vadik.knigopis.createNewBookIntent
import me.vadik.knigopis.dialog.DialogFactory
import me.vadik.knigopis.dialog.createDialogItem
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.BookHeader
import me.vadik.knigopis.model.FinishedBook

class BooksAdapter(
    books: List<Book>,
    private val dialogs: DialogFactory
) : AbstractBooksAdapter(books, R.layout.header, R.layout.user_book) {

    override fun bindHeaderViewHolder(holder: BookHeaderViewHolder, header: BookHeader, i: Int) {
        if (header.title.isEmpty()) {
            holder.setTitle(R.string.books_header_done_other)
        } else {
            holder.setTitle(header.title)
        }
        holder.showTopDivider(i > 0)
    }

    override fun bindItemViewHolder(holder: BookItemViewHolder, book: FinishedBook) {
        holder.setTitle(book.title)
        holder.setAuthor(book.author)
        holder.setNotes(book.notes)
        holder.setOnLongClick { view ->
            val context = view.context
            dialogs.showDialog(
                book.title + " — " + book.author,
                createDialogItem(R.string.user_button_todo, R.drawable.ic_playlist_add) {
                    context.startActivity(context.createNewBookIntent(book.title, book.author))
                },
                createDialogItem(R.string.user_button_done, R.drawable.ic_playlist_add_check) {
                    context.startActivity(context.createNewBookIntent(book.title, book.author, 100))
                }
            )
            true
        }
    }

}