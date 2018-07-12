package com.sirekanyan.knigopis.repository.cache.common

enum class CacheKey {

    PLANNED_BOOKS, FINISHED_BOOKS, SUBSCRIPTIONS, NOTES;

    val storeValue get() = name.toLowerCase()

}