package me.vadik.knigopis.auth

import android.content.Context
import android.content.Intent
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.io2main
import me.vadik.knigopis.logError
import ru.ulogin.sdk.UloginAuthActivity
import java.util.*

private const val PREFS_NAME = "knigopis"
private const val TOKEN_KEY = "token"
private const val ACCESS_TOKEN_KEY = "access_token"

interface KAuth {
    fun isAuthorized(): Boolean
    fun getAccessToken(): String
    fun getTokenRequest(): Intent
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

    override fun getTokenRequest(): Intent {
        return Intent(context, UloginAuthActivity::class.java)
            .putExtra(UloginAuthActivity.FIELDS, arrayOf(TOKEN_KEY))
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
                    preferences.edit().putString(ACCESS_TOKEN_KEY, it.accessToken).apply()
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