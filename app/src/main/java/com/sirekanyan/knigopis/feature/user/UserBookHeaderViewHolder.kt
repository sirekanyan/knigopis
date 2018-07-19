package com.sirekanyan.knigopis.feature.user

import android.view.View
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

    override fun onBind(position: Int, model: BookModel) {
        val header = model as BookHeaderModel
        headerTitle.text = header.title
        headerCount.text = header.count
        headerDivider.showNow(position > 0)
    }

}