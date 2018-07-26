package com.sirekanyan.knigopis.feature.books

import android.view.View
import com.sirekanyan.knigopis.common.android.adapter.CommonViewHolder
import com.sirekanyan.knigopis.common.extensions.*
import com.sirekanyan.knigopis.common.extensions.setProgressSmoothly
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.book.*

class BookDataViewHolder(
    override val containerView: View,
    onClick: (BookDataModel) -> Unit,
    onLongClick: (BookDataModel) -> Unit
) : CommonViewHolder<BookModel>(containerView),
    LayoutContainer {

    private val resources = containerView.resources

    init {
        containerView.setOnClickListener {
            model?.let {
                onClick(it as BookDataModel)
            }
        }
        containerView.setOnLongClickListener {
            model?.let {
                onLongClick(it as BookDataModel)
            }
            true
        }
    }

    override fun onBind(position: Int, model: BookModel) {
        val book = model as BookDataModel
        bookImage.setSquareImage(book.image)
        bookTitle.text = resources.getTitleString(book.title)
        bookAuthor.text = resources.getAuthorString(book.author)
        bookProgress.progress = 0
        if (book.isFinished) {
            bookProgress.hideNow()
        } else {
            bookProgress.showNow()
            bookProgress.setProgressSmoothly(book.priority)
        }
    }

}