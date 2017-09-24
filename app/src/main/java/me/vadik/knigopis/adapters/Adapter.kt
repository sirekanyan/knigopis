package me.vadik.knigopis.adapters

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import me.vadik.knigopis.inflate

class Adapter<T>(
    private val items: List<T>,
    private val layout: (T) -> Int
) {

  val binders = mutableMapOf<@IdRes Int, (View, T) -> Unit>()

  inline fun <reified V : View> bind(@IdRes id: Int, crossinline binder: V.(T) -> Unit): Adapter<T> {
    binders[id] = { view, model ->
      binder(view as V, model)
    }
    return this
  }

  fun build() = object : RecyclerView.Adapter<ViewsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        parent.inflate(viewType).let { rootView ->
          ViewsHolder(rootView, binders.mapValues { (key, _) ->
            rootView.findViewById<View>(key)
          })
        }

    override fun onBindViewHolder(holder: ViewsHolder, position: Int) =
        binders.forEach { (id, binder) ->
          holder.views[id]?.let { view ->
            binder(view, items[position])
          }
        }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = layout(items[position])
  }
}

class ViewsHolder(rootView: View, val views: Map<Int, View>) : RecyclerView.ViewHolder(rootView)
