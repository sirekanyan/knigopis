package com.sirekanyan.knigopis.model

import android.text.format.DateUtils
import com.sirekanyan.knigopis.MAX_BOOK_PRIORITY
import com.sirekanyan.knigopis.MIN_BOOK_PRIORITY
import com.sirekanyan.knigopis.common.functions.createBookImageUrl
import com.sirekanyan.knigopis.common.functions.createUserImageUrl
import com.sirekanyan.knigopis.common.functions.createUserPublicUrl
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
        createUserImageUrl(subUser.id),
        subUser.booksCount.takeIf { it > 0 },
        newBooksCount.takeIf { it > 0 },
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
        user.nickname ?: user.id,
        createUserImageUrl(user.id)
    )

fun BookDataModel.toEditModel(): EditBookModel {
    val progress = if (isFinished) MAX_BOOK_PRIORITY else priority
    val dateModel = if (isFinished) date else EMPTY_DATE
    return EditBookModel(BookAction.EDIT, id, title, author, progress, dateModel, notes)
}

fun EditBookModel.toPlannedBook(): PlannedBookToSend {
    val priority = progress.takeIf { it in (MIN_BOOK_PRIORITY..MAX_BOOK_PRIORITY) }
    return PlannedBookToSend(title, author, notes, priority)
}

fun EditBookModel.toFinishedBook(): FinishedBookToSend =
    FinishedBookToSend(title, author, date.day, date.month, date.year, notes)

fun User.toProfileModel(): ProfileModel =
    ProfileModel(id, name, createUserImageUrl(id), profile.orEmpty(), createUserPublicUrl(id))

fun ProfileModel.toProfile(): Profile =
    Profile(name, profileUrl)