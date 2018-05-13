package me.vadik.knigopis.common.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.vadik.knigopis.utils.inflate
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.BookHeader
import me.vadik.knigopis.model.FinishedBook

private const val HEADER_TYPE = 0
private const val ITEM_TYPE = 1

abstract class AbstractBooksAdapter(
    private val elements: List<Book>,
    @LayoutRes private val headerLayout: Int,
    @LayoutRes private val itemLayout: Int
) : RecyclerView.Adapter<BookViewHolder>() {

    abstract fun bindHeaderViewHolder(holder: BookHeaderViewHolder, header: BookHeader, i: Int)

    abstract fun bindItemViewHolder(holder: BookItemViewHolder, book: FinishedBook)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == HEADER_TYPE) {
            BookHeaderViewHolder(parent.inflate(headerLayout))
        } else {
            BookItemViewHolder(parent.inflate(itemLayout))
        }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val element = elements[position]
        when (holder) {
            is BookHeaderViewHolder -> bindHeaderViewHolder(holder, element as BookHeader, position)
            is BookItemViewHolder -> bindItemViewHolder(holder, element as FinishedBook)
        }
    }

    override fun getItemCount() =
        elements.size

    override fun getItemViewType(position: Int) =
        if (elements[position] is BookHeader) {
            HEADER_TYPE
        } else {
            ITEM_TYPE
        }

}