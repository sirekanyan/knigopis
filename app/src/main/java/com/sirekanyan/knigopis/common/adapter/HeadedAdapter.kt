package com.sirekanyan.knigopis.common.adapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.view.ViewGroup

abstract class HeadedAdapter<T : HeadedModel>(
    itemCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, CommonViewHolder<T>>(itemCallback) {

    private companion object {
        private const val HEADER_TYPE = 0
        private const val DATA_TYPE = 1
    }

    abstract fun onCreateHeaderViewHolder(parent: ViewGroup): CommonViewHolder<T>

    abstract fun onCreateDataViewHolder(parent: ViewGroup): CommonViewHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder<T> =
        if (viewType == HEADER_TYPE) {
            onCreateHeaderViewHolder(parent)
        } else {
            onCreateDataViewHolder(parent)
        }

    override fun onBindViewHolder(holder: CommonViewHolder<T>, position: Int) {
        holder.bind(position, getItem(position))
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).isHeader) HEADER_TYPE else DATA_TYPE

    fun getModelByPosition(position: Int): T =
        getItem(position)

}