package com.sirekanyan.knigopis.repository

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.sirekanyan.knigopis.repository.config.booleanPreference
import com.sirekanyan.knigopis.repository.config.enumPreference

private const val PREFS_NAME = "config"

interface Configuration {
    var theme: Theme
    var bookSorting: BookSorting
    var userSorting: UserSorting
    var crashReportEnabled: Boolean
}

class ConfigurationImpl(context: Application) : Configuration {
    internal val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    override var theme by enumPreference(Theme.DEFAULT)
    override var bookSorting by enumPreference(BookSorting.DEFAULT)
    override var userSorting by enumPreference(UserSorting.DEFAULT)
    override var crashReportEnabled by booleanPreference()
}