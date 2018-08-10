package com.sirekanyan.knigopis.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.sirekanyan.knigopis.repository.config.BooleanPreference
import com.sirekanyan.knigopis.repository.config.IntPreference

private const val PREFS_NAME = "config"

interface Configuration {
    var isDarkTheme: Boolean
    var sortingMode: Int
}

class ConfigurationImpl(context: Application) : Configuration {
    internal val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    override var isDarkTheme by BooleanPreference()
    override var sortingMode by IntPreference()
}