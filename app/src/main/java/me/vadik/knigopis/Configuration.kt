package me.vadik.knigopis

import android.content.Context

private const val PREFS_NAME = "knigopis-dev"
private const val DEV_MODE_KEY = "dev-mode"
private const val DARK_THEME_KEY = "dark-theme"
private const val SORT_MODE_KEY = "sort-mode"

interface Configuration {

    var isDevMode: Boolean

    var isDarkTheme: Boolean

    var sortingMode: Int

}

class ConfigurationImpl(context: Context) : Configuration {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override var isDevMode: Boolean
        get() = prefs.getBoolean(DEV_MODE_KEY, false)
        set(enabled) {
            prefs.edit().putBoolean(DEV_MODE_KEY, enabled).apply()
        }

    override var isDarkTheme: Boolean
        get() = prefs.getBoolean(DARK_THEME_KEY, false)
        set(enabled) {
            prefs.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
        }

    override var sortingMode: Int
        get() = prefs.getInt(SORT_MODE_KEY, 0)
        set(mode) {
            prefs.edit().putInt(SORT_MODE_KEY, mode).apply()
        }

}