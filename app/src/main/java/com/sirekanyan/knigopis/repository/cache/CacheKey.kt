package com.sirekanyan.knigopis.repository.cache

import com.sirekanyan.knigopis.common.extensions.lowercase

enum class CacheKey {

    BOOKS, USERS, NOTES;

    val storeValue get() = name.lowercase

}