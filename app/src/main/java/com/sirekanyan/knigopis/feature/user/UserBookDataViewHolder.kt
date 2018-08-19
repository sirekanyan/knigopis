package com.sirekanyan.knigopis.feature.user

import android.view.View
import com.sirekanyan.knigopis.common.android.adapter.CommonViewHolder
import com.sirekanyan.knigopis.common.extensions.setSquareImage
import com.sirekanyan.knigopis.common.extensions.showNow
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_book.*

class UserBookDataViewHolder(
    override val containerView: View,
    private val onClick: (BookDataModel) -> Unit
) : CommonViewHolder<BookModel>(containerView),
    LayoutContainer {

    init {
        containerView.setOnLongClickListener {
            model?.let { book ->
                onClick(book as BookDataModel)
            }
            true
        }
    }

    override fun onBind(position: Int, model: BookModel) {
        val book = model as BookDataModel
        bookTitle.text = book.title
        bookAuthor.text = book.author
        bookNotes.showNow(book.notes.isNotEmpty())
        bookNotes.text = book.notes
        bookImage.setSquareImage(book.image)
    }

}