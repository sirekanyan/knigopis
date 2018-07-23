package com.sirekanyan.knigopis.repository

import android.content.Context

var isDarkConfiguration = false

private const val PREFS_NAME = "knigopis-dev"
private const val DARK_THEME_KEY = "dark-theme"
private const val SORT_MODE_KEY = "sort-mode"

interface Configuration {

    var isDarkTheme: Boolean

    var sortingMode: Int

}

class ConfigurationImpl(context: Context) : Configuration {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        isDarkConfiguration = isDarkTheme
    }

    override var isDarkTheme: Boolean
        get() = prefs.getBoolean(DARK_THEME_KEY, false)
        set(enabled) {
            isDarkConfiguration = enabled
            prefs.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
        }

    override var sortingMode: Int
        get() = prefs.getInt(SORT_MODE_KEY, 0)
        set(mode) {
            prefs.edit().putInt(SORT_MODE_KEY, mode).apply()
        }

}