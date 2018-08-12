package com.sirekanyan.knigopis.repository

import android.content.Context
import com.sirekanyan.knigopis.common.extensions.io2main
import io.reactivex.Completable

private const val PREFS_NAME = "knigopis"
private const val TOKEN_KEY = "token"
private const val ACCESS_TOKEN_KEY = "access_token"
private const val USER_PROFILE = "user_profile"

interface AuthRepository {
    fun isAuthorized(): Boolean
    fun loadAccessToken(): Completable
    fun getAccessToken(): String
    fun getUserProfile(): String?
    fun saveToken(token: String)
    fun clear()
}

class AuthRepositoryImpl(
    context: Context,
    private val api: Endpoint
) : AuthRepository {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isAuthorized(): Boolean =
        preferences.contains(ACCESS_TOKEN_KEY)

    override fun getAccessToken(): String =
        preferences.getString(ACCESS_TOKEN_KEY, "")

    override fun getUserProfile(): String? =
        preferences.getString(USER_PROFILE, null)

    override fun saveToken(token: String) {
        preferences.edit().putString(TOKEN_KEY, token).apply()
    }

    override fun loadAccessToken(): Completable {
        val token = preferences.getString(TOKEN_KEY, null)
        return if (token != null && !isAuthorized()) {
            api.getCredentials(token)
                .io2main()
                .doOnSuccess {
                    preferences.edit()
                        .putString(ACCESS_TOKEN_KEY, it.accessToken)
                        .putString(USER_PROFILE, it.user.fixedProfile)
                        .apply()
                }
                .toCompletable()
        } else {
            Completable.complete()
        }
    }

    override fun clear() {
        preferences.edit().remove(TOKEN_KEY).remove(ACCESS_TOKEN_KEY).apply()
    }

}