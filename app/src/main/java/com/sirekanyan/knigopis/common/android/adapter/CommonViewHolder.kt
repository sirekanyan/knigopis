package com.sirekanyan.knigopis.common.android.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sirekanyan.knigopis.common.android.toast.CommonView

abstract class CommonViewHolder<T>(view: View) : RecyclerView.ViewHolder(view), CommonView {

    protected var model: T? = null

    abstract fun onBind(position: Int, model: T)

    fun bind(position: Int, model: T) {
        this.model = model
        onBind(position, model)
    }

}