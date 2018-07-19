package com.sirekanyan.knigopis.common.view.header

import android.app.Application
import android.view.View
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.extensions.showNow
import com.sirekanyan.knigopis.common.orDefault
import com.sirekanyan.knigopis.model.BookModel
import kotlinx.android.synthetic.main.header.view.*

class StickyHeaderImpl(
    private val app: Application,
    private val books: List<BookModel>
) : StickyHeader {

    override fun getHeaderPositionForItem(itemPosition: Int) = itemPosition

    override fun getHeaderLayout(headerPosition: Int) = R.layout.header

    override fun isHeader(itemPosition: Int) = books[itemPosition].isHeader

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val group = books[headerPosition].group
        header.headerTitle.text =
                group.title.orDefault(app.getString(R.string.books_header_done_other))
        header.headerCount.also { headerCount ->
            headerCount.text = app.resources.getQuantityString(
                R.plurals.common_header_books,
                group.count,
                group.count
            )
            headerCount.showNow()
        }
        header.headerBottomDivider.showNow()
    }

}