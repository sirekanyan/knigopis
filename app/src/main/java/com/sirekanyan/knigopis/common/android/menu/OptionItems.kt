package com.sirekanyan.knigopis.common.android.menu

inline fun <reified T> optionIds(): List<Int> where T : Enum<T>, T : OptionItem =
    enumValues<T>().map(OptionItem::id)

inline fun <reified T> getOption(id: Int): T where T : Enum<T>, T : OptionItem =
    enumValues<T>().find { it.id == id } ?: enumValues<T>().first()