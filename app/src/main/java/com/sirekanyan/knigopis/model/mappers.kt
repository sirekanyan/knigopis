package com.sirekanyan.knigopis.model

import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.SuperscriptSpan
import com.sirekanyan.knigopis.MAX_BOOK_PRIORITY
import com.sirekanyan.knigopis.MIN_BOOK_PRIORITY
import com.sirekanyan.knigopis.common.functions.createBookImageUrl
import com.sirekanyan.knigopis.model.dto.*

fun PlannedBook.toBookModel(group: BookGroupModel) =
    BookDataModel(
        id,
        group,
        title,
        author,
        false,
        priority,
        EMPTY_DATE,
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
        subUser.photo,
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

fun BookDataModel.toEditModel(): EditBookModel =
    EditBookModel(
        BookAction.EDIT,
        id,
        title,
        author,
        if (isFinished) MAX_BOOK_PRIORITY else priority,
        if (isFinished) date else EMPTY_DATE,
        notes
    )

fun EditBookModel.toPlannedBook(): PlannedBookToSend =
    PlannedBookToSend(
        title,
        author,
        notes,
        progress.takeIf { it in (MIN_BOOK_PRIORITY..MAX_BOOK_PRIORITY) }
    )

fun EditBookModel.toFinishedBook(): FinishedBookToSend =
    FinishedBookToSend(
        title,
        author,
        date.day,
        date.month,
        date.year,
        notes
    )

fun User.toProfileModel(): ProfileModel =
    ProfileModel(id, name, photo, profile.orEmpty(), fixedProfile)

fun ProfileModel.toProfile(): Profile =
    Profile(name, profileUrl)