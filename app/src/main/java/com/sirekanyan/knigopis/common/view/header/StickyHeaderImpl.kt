package com.sirekanyan.knigopis.common.view.header

import android.view.View
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.adapter.HeadedAdapter
import com.sirekanyan.knigopis.common.extensions.showNow
import com.sirekanyan.knigopis.model.BookModel
import kotlinx.android.synthetic.main.header.view.*

class StickyHeaderImpl(private val adapter: HeadedAdapter<BookModel>) : StickyHeader {

    override fun getHeaderPositionForItem(itemPosition: Int) = itemPosition

    override fun getHeaderLayout(headerPosition: Int) = R.layout.header

    override fun isHeader(itemPosition: Int) = adapter.getModelByPosition(itemPosition).isHeader

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val group = adapter.getModelByPosition(headerPosition).group
        header.headerTitle.text = group.title
        header.headerCount.text = group.count
        header.headerBottomDivider.showNow()
    }

}