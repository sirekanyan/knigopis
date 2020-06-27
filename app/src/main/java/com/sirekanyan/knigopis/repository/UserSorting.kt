package com.sirekanyan.knigopis.repository

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.menu.OptionItem

enum class UserSorting(
    @IdRes override val id: Int,
    @StringRes override val title: Int
) : OptionItem {

    DEFAULT(R.id.option_sort_users_by_time, R.string.main_option_sort_users_by_time),
    BY_NAME(R.id.option_sort_users_by_name, R.string.main_option_sort_users_by_name),
    BY_COUNT(R.id.option_sort_users_by_count, R.string.main_option_sort_users_by_count),
    BY_NEW_COUNT(R.id.option_sort_users_by_new_count, R.string.main_option_sort_users_by_new_count);

}