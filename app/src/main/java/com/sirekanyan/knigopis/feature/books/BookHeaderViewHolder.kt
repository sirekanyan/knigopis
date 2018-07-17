package com.sirekanyan.knigopis.feature.books

import android.view.View
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.adapter.CommonViewHolder
import com.sirekanyan.knigopis.common.extensions.showNow
import com.sirekanyan.knigopis.model.BookHeaderModel
import com.sirekanyan.knigopis.model.BookModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header.*

class BookHeaderViewHolder(
    override val containerView: View
) : CommonViewHolder<BookModel>(containerView),
    LayoutContainer {

    private val resources = containerView.context.resources

    override fun onBind(position: Int, model: BookModel) {
        val header = model as BookHeaderModel
        headerTitle.text = header.title
        headerDivider.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        headerCount.text = resources.getQuantityString(
            R.plurals.common_header_books,
            header.count,
            header.count
        )
        headerCount.showNow(header.count > 0)
    }

}