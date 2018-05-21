package me.vadik.knigopis.repository.cache

import android.content.Context
import android.content.Context.MODE_PRIVATE
import io.reactivex.Maybe

private const val PREFERENCES_NAME = "knigopis_thumbnails"

interface BookCoverCache {
    fun put(bookId: String, url: String)
    fun find(bookId: String): Maybe<String>
}

class BookCoverCacheImpl(context: Context) : BookCoverCache {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

    override fun put(bookId: String, url: String) =
        preferences.edit().putString(bookId, url).apply()

    override fun find(bookId: String): Maybe<String> =
        Maybe.fromCallable {
            preferences.getString(bookId, null)
        }
}