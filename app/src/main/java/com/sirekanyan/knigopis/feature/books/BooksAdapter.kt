package com.sirekanyan.knigopis.feature.books

import android.view.ViewGroup
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.adapter.HeadedAdapter
import com.sirekanyan.knigopis.common.extensions.inflate
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel

class BooksAdapter(
    private val onClick: (BookDataModel) -> Unit,
    private val onLongClick: (BookDataModel) -> Unit
) : HeadedAdapter<BookModel>(BookItemCallback()) {

    override fun onCreateHeaderViewHolder(parent: ViewGroup) =
        BookHeaderViewHolder(parent.inflate(R.layout.header))

    override fun onCreateDataViewHolder(parent: ViewGroup) =
        BookDataViewHolder(parent.inflate(R.layout.book), onClick, onLongClick)

}