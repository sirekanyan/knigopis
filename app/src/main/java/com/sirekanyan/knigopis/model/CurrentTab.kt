package com.sirekanyan.knigopis.model

import androidx.annotation.IdRes
import com.sirekanyan.knigopis.R

enum class CurrentTab(@IdRes val itemId: Int) {

    BOOKS_TAB(R.id.navigation_home),
    USERS_TAB(R.id.navigation_users),
    NOTES_TAB(R.id.navigation_notes);

    companion object {
        fun getByItemId(@IdRes itemId: Int) =
            checkNotNull(values().find { it.itemId == itemId })
    }
}