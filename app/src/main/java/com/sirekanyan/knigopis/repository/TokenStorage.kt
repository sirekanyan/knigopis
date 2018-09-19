package com.sirekanyan.knigopis.repository

import android.content.Context

private const val PREFS_NAME = "knigopis"
private const val TOKEN_KEY = "token"
private const val ACCESS_TOKEN_KEY = "access_token"

interface TokenStorage {
    var token: String?
    var accessToken: String?
    fun clearTokens()
}

class TokenStorageImpl(context: Context) : TokenStorage {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override var token: String?
        get() = preferences.getString(TOKEN_KEY, null)
        set(value) = preferences.edit().putString(TOKEN_KEY, value).apply()

    override var accessToken: String?
        get() = preferences.getString(ACCESS_TOKEN_KEY, null)
        set(value) = preferences.edit().putString(ACCESS_TOKEN_KEY, value).apply()

    override fun clearTokens() {
        preferences.edit().remove(TOKEN_KEY).remove(ACCESS_TOKEN_KEY).apply()
    }

}