package com.sirekanyan.knigopis.feature.user

import android.view.ViewGroup
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.adapter.HeadedAdapter
import com.sirekanyan.knigopis.common.android.adapter.SimpleItemCallback
import com.sirekanyan.knigopis.common.extensions.inflate
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel

class UserBooksAdapter(
    private val onLongClick: (BookDataModel) -> Unit
) : HeadedAdapter<BookModel>(SimpleItemCallback { it.id }) {

    override fun onCreateHeaderViewHolder(parent: ViewGroup) =
        UserBookHeaderViewHolder(parent.inflate(R.layout.header))

    override fun onCreateDataViewHolder(parent: ViewGroup) =
        UserBookDataViewHolder(parent.inflate(R.layout.user_book), onLongClick)

}