package com.sirekanyan.knigopis.repository.cache.common

enum class CacheKey {

    BOOKS, USERS, NOTES;

    val storeValue get() = name.toLowerCase()

}