package com.sirekanyan.knigopis.repository.config

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.sirekanyan.knigopis.repository.ConfigurationImpl
import kotlin.reflect.KProperty

abstract class AbstractPreference<T>(
    private val load: SharedPreferences.(key: String) -> T,
    private val save: Editor.(key: String, value: T) -> Editor
) {

    operator fun getValue(config: ConfigurationImpl, prop: KProperty<*>): T =
        config.prefs.load(prop.name)

    operator fun setValue(config: ConfigurationImpl, prop: KProperty<*>, value: T) =
        config.prefs.edit().save(prop.name, value).apply()

}

class IntPreference : AbstractPreference<Int>(
    { key -> getInt(key, 0) },
    { key, value -> putInt(key, value) }
)

class BooleanPreference : AbstractPreference<Boolean>(
    { key -> getBoolean(key, false) },
    { key, value -> putBoolean(key, value) }
)