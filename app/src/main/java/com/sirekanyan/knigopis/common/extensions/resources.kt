package com.sirekanyan.knigopis.common.extensions

import android.content.res.Resources
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.orDefault

fun Resources.getTitleString(title: String): String {
    return title.orDefault(getString(R.string.common_book_notitle))
}

fun Resources.getAuthorString(author: String): String {
    return author.orDefault(getString(R.string.common_book_noauthor))
}

fun Resources.getFullTitleString(title: String, author: String): String {
    return when {
        author.isEmpty() -> getTitleString(title)
        else -> "${getTitleString(title)} — ${getAuthorString(author)}"
    }
}