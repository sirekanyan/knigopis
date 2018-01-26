package me.vadik.knigopis

import android.content.Context

private const val PREFS_NAME = "knigopis-dev"
private const val DEV_MODE_KEY = "dev-mode"

interface Configuration {
    fun isDevMode(): Boolean
    fun setDevMode(enabled: Boolean)
}

class ConfigurationImpl(context: Context) : Configuration {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isDevMode() =
        prefs.getBoolean(DEV_MODE_KEY, false)

    override fun setDevMode(enabled: Boolean) =
        prefs.edit().putBoolean(DEV_MODE_KEY, enabled).apply()
}