package me.vadik.knigopis.adapters

import android.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import me.vadik.knigopis.*
import me.vadik.knigopis.api.BookCoverSearch
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.dialog.DialogFactory
import me.vadik.knigopis.dialog.createDialogItem
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.BookHeader
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.PlannedBook

class BooksAdapter(
    private val coverSearch: BookCoverSearch,
    private val api: Endpoint,
    private val auth: KAuth,
    private val router: Router,
    private val dialogs: DialogFactory
) {

    fun build(books: MutableList<Book>) = Adapter(books) {
        if (it is BookHeader) {
            R.layout.header
        } else {
            R.layout.book
        }
    }
        .bind2<View>(R.id.book_item_container) { bookIndex, adapter ->
            val onDeleteConfirmed = { book: Book ->
                val index = books.indexOfFirst { it.id == book.id }
                if (index >= 0) {
                    when (book) {
                        is FinishedBook -> api.deleteFinishedBook(book.id, auth.getAccessToken())
                        is PlannedBook -> api.deletePlannedBook(book.id, auth.getAccessToken())
                        else -> throw UnsupportedOperationException()
                    }
                        .io2main()
                        .subscribe({}, {
                            context.toast(R.string.books_error_delete)
                            logError("cannot delete finished book", it)
                        })
                    books.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            }
            val onDeleteClicked = { book: Book ->
                AlertDialog.Builder(context)
                    .setTitle(R.string.books_title_confirm_delete)
                    .setMessage(
                        context.getString(
                            R.string.books_message_confirm_delete,
                            book.fullTitle
                        )
                    )
                    .setNegativeButton(R.string.common_button_cancel) { d, _ -> d.dismiss() }
                    .setPositiveButton(R.string.books_button_confirm_delete) { d, _ ->
                        onDeleteConfirmed(book)
                        d.dismiss()
                    }
                    .show()
            }
            val book = books[bookIndex]
            setOnClickListener {
                router.openEditBookScreen(book)
            }
            setOnLongClickListener {
                dialogs.showDialog(
                    book.fullTitle,
                    createDialogItem(R.string.books_button_edit, R.drawable.ic_edit) {
                        router.openEditBookScreen(book)
                    },
                    createDialogItem(R.string.books_button_delete, R.drawable.ic_delete) {
                        onDeleteClicked(book)
                    }
                )
                true
            }
        }
        .bind<ProgressBar>(R.id.book_progress) {
            val book = books[it]
            progress = 0
            if (book is PlannedBook) {
                showNow()
                setProgressSmoothly(book.priority)
            } else {
                hideNow()
            }
        }
        .bind<ImageView>(R.id.book_image) {
            coverSearch.search(books[it])
                .subscribe({ coverUrl ->
                    Glide.with(context)
                        .load(coverUrl)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .placeholder(R.drawable.rectangle_placeholder_background)
                        )
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(this)
                }, {
                    logError("cannot load thumbnail", it)
                    Glide.with(context)
                        .load(R.drawable.rectangle_placeholder_background)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(this)
                })
        }
        .bind<TextView>(R.id.book_title) {
            text = books[it].titleOrDefault
        }
        .bind<View>(R.id.header_divider) {
            visibility = if (it == 0) View.INVISIBLE else View.VISIBLE
        }
        .bind<TextView>(R.id.book_author) {
            text = books[it].authorOrDefault
        }
        .get()
}