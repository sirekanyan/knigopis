package me.vadik.knigopis.adapters

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import me.vadik.knigopis.inflate

class Adapter<in T, in V : View>(@IdRes val id: Int, val bind: V.(T) -> Unit)

class ViewHolder(rootView: View, val views: List<*>) : RecyclerView.ViewHolder(rootView)

fun <T, V : View> createAdapter(
    items: List<T>,
    @LayoutRes itemLayout: Int,
    vararg adapters: Adapter<T, V>
) =
    object : RecyclerView.Adapter<ViewHolder>() {
      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
          parent.inflate(itemLayout).let { rootView ->
            ViewHolder(rootView, adapters.map {
              @Suppress("UNCHECKED_CAST")
              rootView.findViewById<View>(it.id) as V
            })
          }

      override fun onBindViewHolder(holder: ViewHolder, position: Int) =
          adapters.forEachIndexed { index, adapter ->
            @Suppress("UNCHECKED_CAST")
            adapter.bind.invoke(holder.views[index] as V, items[position])
          }

      override fun getItemCount() = items.size
    }