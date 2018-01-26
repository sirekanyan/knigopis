package me.vadik.knigopis.model

import android.support.annotation.IdRes
import me.vadik.knigopis.R

enum class CurrentTab(@IdRes val itemId: Int) {

    HOME_TAB(R.id.navigation_home),
    USERS_TAB(R.id.navigation_users),
    NOTES_TAB(R.id.navigation_notes);

    companion object {
        fun getByItemId(@IdRes itemId: Int) =
            checkNotNull(values().find { it.itemId == itemId })
    }
}