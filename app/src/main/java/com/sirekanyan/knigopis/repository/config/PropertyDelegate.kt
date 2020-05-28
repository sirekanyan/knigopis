package com.sirekanyan.knigopis.repository.config

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import com.sirekanyan.knigopis.repository.ConfigurationImpl
import kotlin.reflect.KProperty

class PreferenceDelegate<T>(
    private val load: SharedPreferences.(key: String) -> T,
    private val save: Editor.(key: String, value: T) -> Editor
) {

    operator fun getValue(config: ConfigurationImpl, prop: KProperty<*>): T =
        config.prefs.load(prop.name)

    operator fun setValue(config: ConfigurationImpl, prop: KProperty<*>, value: T) =
        config.prefs.edit().save(prop.name, value).apply()

}

fun intPreference(): PreferenceDelegate<Int> =
    PreferenceDelegate({ key -> getInt(key, 0) }, { key, value -> putInt(key, value) })

@Suppress("Unused")
fun booleanPreference(): PreferenceDelegate<Boolean> =
    PreferenceDelegate({ key -> getBoolean(key, false) }, { key, value -> putBoolean(key, value) })

inline fun <reified T : Enum<T>> enumPreference(default: T): PreferenceDelegate<T> =
    PreferenceDelegate(
        { key ->
            getString(key, null)?.let { stringValue ->
                try {
                    enumValueOf<T>(stringValue)
                } catch (exception: IllegalArgumentException) {
                    Log.e("knigopis", "cannot deserialize enum", exception)
                    null
                }
            } ?: default
        },
        { key, value ->
            putString(key, value.toString())
        }
    )