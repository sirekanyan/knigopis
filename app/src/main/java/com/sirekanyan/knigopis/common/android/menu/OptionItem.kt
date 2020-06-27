package com.sirekanyan.knigopis.common.android.menu

import androidx.annotation.IdRes
import androidx.annotation.StringRes

interface OptionItem {

    @get:IdRes
    val id: Int

    @get:StringRes
    val title: Int

}