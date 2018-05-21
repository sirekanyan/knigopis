package me.vadik.knigopis.repository.cache.common

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Maybe
import java.lang.reflect.Type

private const val PREFS_NAME = "cached"

inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

interface CommonCache {

    fun <T> saveToJson(key: CacheKey, books: List<T>): Completable

    fun <T> getFromJson(key: CacheKey, type: Type): Maybe<T>

}

class CommonCacheImpl(
    context: Context,
    private val gson: Gson
) : CommonCache {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun <T> getFromJson(key: CacheKey, type: Type): Maybe<T> =
        Maybe.fromCallable {
            prefs.getString(key.storeValue, null)?.let { json ->
                gson.fromJson<T>(json, type)
            }
        }

    override fun <T> saveToJson(key: CacheKey, books: List<T>): Completable =
        Completable.fromAction {
            prefs.edit()
                .putString(key.storeValue, gson.toJson(books))
                .apply()
        }

}