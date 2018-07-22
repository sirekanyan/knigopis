package com.sirekanyan.knigopis.model

import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.SuperscriptSpan
import com.sirekanyan.knigopis.common.MAX_BOOK_PRIORITY
import com.sirekanyan.knigopis.common.functions.createBookImageUrl
import com.sirekanyan.knigopis.model.dto.FinishedBook
import com.sirekanyan.knigopis.model.dto.Note
import com.sirekanyan.knigopis.model.dto.PlannedBook
import com.sirekanyan.knigopis.model.dto.Subscription

fun PlannedBook.toBookModel(group: BookGroupModel) =
    BookDataModel(
        id,
        group,
        title,
        author,
        false,
        priority,
        null,
        notes
    )

fun FinishedBook.toBookModel(group: BookGroupModel) =
    BookDataModel(
        id,
        group,
        title,
        author,
        true,
        MAX_BOOK_PRIORITY,
        DateModel(readYear, readMonth, readDay),
        notes
    )

fun Subscription.toUserModel() =
    UserModel(
        subUser.id,
        subUser.name,
        subUser.avatar,
        subUser.booksCount.takeIf { it > 0 }?.toString(),
        newBooksCount.takeIf { it > 0 }?.let { count ->
            val str = "+$count"
            SpannableString(str).also {
                it.setSpan(SuperscriptSpan(), 0, str.length, 0)
            }
        },
        subUser.profiles
    )

fun Note.toNoteModel() =
    NoteModel(
        id,
        title,
        author,
        createBookImageUrl(title),
        notes,
        DateUtils.getRelativeTimeSpanString(fixedCreatedAt.time).toString(),
        user.id,
        user.name,
        user.avatarUrl
    )
