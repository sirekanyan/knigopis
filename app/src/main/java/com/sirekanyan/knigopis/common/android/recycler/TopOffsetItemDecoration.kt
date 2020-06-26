package com.sirekanyan.knigopis.common.android.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sirekanyan.knigopis.R

class TopOffsetItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val offset = context.resources.getDimensionPixelSize(R.dimen.bottom_navigation_height)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == 0) {
            outRect.top = offset
        }
    }

}