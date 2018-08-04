package com.sirekanyan.knigopis.common.extensions

import java.util.*

private val random = Random()

fun <T> List<T>.random(): T? {
    if (size == 0) return null
    return get(random.nextInt(size))
}