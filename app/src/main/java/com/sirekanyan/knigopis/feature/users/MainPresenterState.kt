package com.sirekanyan.knigopis.feature.users

import android.os.Bundle

private const val CURRENT_TAB_KEY = "current_tab"

fun Bundle.getMainState(): MainPresenterState =
    MainPresenterState(getInt(CURRENT_TAB_KEY))

fun Bundle.saveMainState(state: MainPresenterState) {
    putInt(CURRENT_TAB_KEY, state.currentTab)
}

class MainPresenterState(val currentTab: Int)