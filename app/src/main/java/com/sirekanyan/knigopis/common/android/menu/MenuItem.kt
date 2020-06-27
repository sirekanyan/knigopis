package com.sirekanyan.knigopis.common.android.menu

import android.view.Menu
import android.view.MenuItem

fun MenuItem.addAll(items: Array<out OptionItem>) {
    checkNotNull(subMenu) { "submenu is not specified for item $title" }
    items.forEach {
        subMenu.add(Menu.NONE, it.id, Menu.NONE, it.title)
    }
    subMenu.setGroupCheckable(Menu.NONE, true, true)
}