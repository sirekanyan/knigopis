package com.sirekanyan.knigopis.common

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.sirekanyan.knigopis.common.extensions.inflate

class Adapter<T>(
    private val items: List<T>,
    private val layout: (T) -> Int
) {

    val binders = mutableMapOf<@IdRes Int, (View, Int) -> Unit>()
    @Suppress("MemberVisibilityCanPrivate")
    val recyclerViewAdapter = object : RecyclerView.Adapter<ViewsHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            parent.inflate(viewType).let { rootView ->
                ViewsHolder(rootView, binders.mapValues { (key, _) ->
                    rootView.findViewById<View>(key)
                })
            }

        override fun onBindViewHolder(holder: ViewsHolder, position: Int) =
            binders.forEach { (id, binder) ->
                holder.views[id]?.let { view ->
                    binder(view, position)
                }
            }

        override fun getItemCount() = items.size

        override fun getItemViewType(position: Int) = layout(items[position])
    }

    inline fun <reified V : View> bind(@IdRes id: Int, crossinline binder: V.(Int) -> Unit): Adapter<T> {
        binders[id] = { view, position ->
            binder(view as V, position)
        }
        return this
    }

    inline fun <reified V : View> bind2(@IdRes id: Int, crossinline binder: V.(Int, RecyclerView.Adapter<ViewsHolder>) -> Unit): Adapter<T> {
        binders[id] = { view, position ->
            binder(view as V, position, recyclerViewAdapter)
        }
        return this
    }

    fun get() = recyclerViewAdapter
}

class ViewsHolder(rootView: View, val views: Map<Int, View>) : RecyclerView.ViewHolder(rootView)
