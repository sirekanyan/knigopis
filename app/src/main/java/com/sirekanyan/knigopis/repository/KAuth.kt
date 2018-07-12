package com.sirekanyan.knigopis.repository

import android.content.Context
import android.content.Intent
import com.sirekanyan.knigopis.common.io2main
import com.sirekanyan.knigopis.common.logError
import com.sirekanyan.knigopis.repository.api.Endpoint
import ru.ulogin.sdk.UloginAuthActivity
import java.util.*

private const val PREFS_NAME = "knigopis"
private const val TOKEN_KEY = "token"
private const val ACCESS_TOKEN_KEY = "access_token"
private const val USER_PROFILE = "user_profile"

interface KAuth {
    fun isAuthorized(): Boolean
    fun getAccessToken(): String
    fun getTokenRequest(): Intent
    fun getUserProfile(): String?
    fun saveTokenResponse(data: Intent)
    fun requestAccessToken(onSuccess: () -> Unit)
    fun logout()
}

class KAuthImpl(
    private val context: Context,
    private val api: Endpoint
) : KAuth {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isAuthorized() = preferences.contains(ACCESS_TOKEN_KEY)

    override fun getAccessToken(): String = preferences.getString(ACCESS_TOKEN_KEY, "")

    override fun getTokenRequest() = Intent(context, UloginAuthActivity::class.java)

    override fun getUserProfile(): String? {
        return preferences.getString(USER_PROFILE, null)
    }

    override fun saveTokenResponse(data: Intent) {
        val userData = data.getSerializableExtra(UloginAuthActivity.USERDATA) as HashMap<*, *>
        preferences.edit().putString(TOKEN_KEY, userData[TOKEN_KEY].toString()).apply()
    }

    override fun requestAccessToken(onSuccess: () -> Unit) {
        val token = preferences.getString(TOKEN_KEY, null)
        if (token != null && !isAuthorized()) {
            api.getCredentials(token)
                .io2main()
                .subscribe({
                    preferences.edit()
                        .putString(ACCESS_TOKEN_KEY, it.accessToken)
                        .putString(
                            USER_PROFILE,
                            it.user.fixedProfile
                        )
                        .apply()
                    onSuccess()
                }, {
                    logError("cannot get credentials", it)
                })
        }
    }

    override fun logout() {
        preferences.edit().remove(TOKEN_KEY).remove(ACCESS_TOKEN_KEY).apply()
    }
}