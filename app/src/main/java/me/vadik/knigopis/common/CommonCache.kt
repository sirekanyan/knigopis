package me.vadik.knigopis.common

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Maybe
import java.lang.reflect.Type

private const val PREFS_NAME = "cache1"

inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

interface CommonCache {

    fun <T> saveToJson(key: String, books: List<T>): Completable

    fun <T> getFromJson(key: String, type: Type): Maybe<T>

}

class CommonCacheImpl(
    context: Context,
    private val gson: Gson
) : CommonCache {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun <T> getFromJson(key: String, type: Type): Maybe<T> =
        Maybe.fromCallable {
            prefs.getString(key, null)?.let { json ->
                gson.fromJson<T>(json, type)
            }
        }

    override fun <T> saveToJson(key: String, books: List<T>): Completable =
        Completable.fromAction {
            prefs.edit()
                .putString(key, gson.toJson(books))
                .apply()
        }

}