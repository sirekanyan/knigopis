package com.sirekanyan.knigopis.feature.user

import android.view.View
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.adapter.CommonViewHolder
import com.sirekanyan.knigopis.common.extensions.showNow
import com.sirekanyan.knigopis.model.BookHeaderModel
import com.sirekanyan.knigopis.model.BookModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header.*

class UserBookHeaderViewHolder(
    override val containerView: View
) : CommonViewHolder<BookModel>(containerView),
    LayoutContainer {

    private val context = containerView.context
    private val defaultTitle = context.getString(R.string.books_header_done_other)

    override fun onBind(position: Int, model: BookModel) {
        val header = model as BookHeaderModel
        headerTitle.text = header.title.takeUnless(String::isEmpty) ?: defaultTitle
        headerCount.text = context.resources.getQuantityString(
            R.plurals.common_header_books,
            header.count,
            header.count
        )
        headerCount.showNow()
        headerDivider.showNow(position > 0)
    }

}