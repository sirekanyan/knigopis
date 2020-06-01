package com.sirekanyan.knigopis.repository

import androidx.annotation.IdRes
import com.sirekanyan.knigopis.R

enum class Sorting(@IdRes val id: Int) {

    DEFAULT(R.id.option_sort_by_progress),
    BY_TIME(R.id.option_sort_by_time),
    BY_TITLE(R.id.option_sort_by_title),
    BY_AUTHOR(R.id.option_sort_by_author);

    companion object {
        fun getById(@IdRes id: Int): Sorting = values().find { it.id == id } ?: DEFAULT
    }

}