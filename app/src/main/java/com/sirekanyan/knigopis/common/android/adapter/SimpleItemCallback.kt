package com.sirekanyan.knigopis.common.android.adapter

import androidx.recyclerview.widget.DiffUtil.ItemCallback

class SimpleItemCallback<T>(private val getId: (T) -> String) : ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) = getId(oldItem) == getId(newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T) = areItemsTheSame(oldItem, newItem)

}