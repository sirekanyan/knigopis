package com.sirekanyan.knigopis.feature.users

import android.os.Bundle
import com.sirekanyan.knigopis.model.CurrentTab

private const val CURRENT_TAB_KEY = "current_tab"

fun Bundle.getMainState(): MainPresenterState =
    MainPresenterState(CurrentTab.getByItemId(getInt(CURRENT_TAB_KEY)))

fun Bundle.saveMainState(state: MainPresenterState) {
    putInt(CURRENT_TAB_KEY, state.currentTab.itemId)
}

class MainPresenterState(val currentTab: CurrentTab)