package me.vadik.knigopis

import android.support.annotation.IdRes

enum class CurrentTab(@IdRes val itemId: Int) {

  HOME_TAB(R.id.navigation_home),
  DONE_TAB(R.id.navigation_done),
  TODO_TAB(R.id.navigation_todo);

  companion object {
    fun getByItemId(@IdRes itemId: Int) =
        checkNotNull(values().find { it.itemId == itemId })
  }
}