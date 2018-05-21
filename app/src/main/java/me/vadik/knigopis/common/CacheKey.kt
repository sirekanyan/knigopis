package me.vadik.knigopis.common

enum class CacheKey {

    PLANNED_BOOKS, FINISHED_BOOKS, SUBSCRIPTIONS;

    val storeValue get() = name.toLowerCase()

}