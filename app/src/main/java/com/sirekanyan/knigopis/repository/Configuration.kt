package com.sirekanyan.knigopis.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.sirekanyan.knigopis.repository.config.enumPreference
import com.sirekanyan.knigopis.repository.config.intPreference

private const val PREFS_NAME = "config"

interface Configuration {
    var theme: Theme
    var sortingMode: Int
}

class ConfigurationImpl(context: Application) : Configuration {
    internal val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    override var theme by enumPreference(Theme.DEFAULT)
    override var sortingMode by intPreference()
}