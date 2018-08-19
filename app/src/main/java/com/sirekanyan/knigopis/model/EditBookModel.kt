package com.sirekanyan.knigopis.model

import android.os.Parcelable
import com.sirekanyan.knigopis.MAX_BOOK_PRIORITY
import kotlinx.android.parcel.Parcelize

val EMPTY_BOOK = EditBookModel(BookAction.NEW)

fun createTodoBook(title: String, author: String, notes: String) =
    EditBookModel(BookAction.COPY, title = title, author = author, notes = notes)

fun createDoneBook(title: String, author: String) =
    EditBookModel(BookAction.COPY, title = title, author = author, progress = MAX_BOOK_PRIORITY)

@Parcelize
class EditBookModel(
    val action: BookAction,
    val id: String? = null,
    val title: String = "",
    val author: String = "",
    val progress: Int = 0,
    val date: DateModel = EMPTY_DATE,
    val notes: String = ""
) : Parcelable {
    val isFinished get() = id != null && progress == MAX_BOOK_PRIORITY
    val isPlanned get() = id != null && progress != MAX_BOOK_PRIORITY
}