package com.sirekanyan.knigopis.repository

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.menu.OptionItem

enum class BookSorting(
    @IdRes override val id: Int,
    @StringRes override val title: Int
) : OptionItem {

    DEFAULT(R.id.option_sort_by_time, R.string.main_option_sort_by_time),
    BY_PROGRESS(R.id.option_sort_by_progress, R.string.main_option_sort_by_progress),
    BY_TITLE(R.id.option_sort_by_title, R.string.main_option_sort_by_title),
    BY_AUTHOR(R.id.option_sort_by_author, R.string.main_option_sort_by_author);

}