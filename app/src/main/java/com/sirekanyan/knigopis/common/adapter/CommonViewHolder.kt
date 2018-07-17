package com.sirekanyan.knigopis.common.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class CommonViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    protected var model: T? = null

    abstract fun onBind(position: Int, model: T)

    fun bind(position: Int, model: T) {
        this.model = model
        onBind(position, model)
    }

}