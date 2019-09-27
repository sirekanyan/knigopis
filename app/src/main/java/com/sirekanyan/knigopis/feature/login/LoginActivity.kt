package com.sirekanyan.knigopis.feature.login

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.permissions.PermissionResult
import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.setDarkTheme
import com.sirekanyan.knigopis.common.functions.createLoginIntent
import com.sirekanyan.knigopis.dependency.providePresenter
import com.sirekanyan.knigopis.feature.startMainActivity
import ru.ulogin.sdk.UloginAuthActivity

private const val MARKET_URI = "market://details?id="
private const val GOOGLE_PLAY_URI = "https://play.google.com/store/apps/details?id="
private const val LOGIN_REQUEST_CODE = 0

fun Context.startLoginActivity() {
    startActivity(Intent(this, LoginActivity::class.java))
}

class LoginActivity : AppCompatActivity(), LoginPresenter.Router {

    private val presenter by lazy { providePresenter() }
    private val auth by lazy { app.authRepository }

    override fun onCreate(savedInstanceState: Bundle?) {
        setDarkTheme(app.config.isDarkTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        presenter.init()
    }

    override fun onResume() {
        super.onResume()
        intent?.data?.findParameter("token")?.let { token ->
            intent = null
            auth.saveToken(token)
            startMainActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        if (permissions.size == 1 && results.size == 1) {
            PermissionResult.create(requestCode, permissions.single(), results.single())?.let {
                presenter.onPermissionResult(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOGIN_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    val userData = data.getSerializableExtra(UloginAuthActivity.USERDATA)
                    val token = (userData as HashMap<*, *>)["token"].toString()
                    auth.saveToken(token)
                    startMainActivity()
                }
            }
        }
    }

    override fun openBrowser(website: Website): Boolean {
        val toolbarColor = ContextCompat.getColor(this, website.color)
        val customTabsIntent = CustomTabsIntent.Builder().setToolbarColor(toolbarColor).build()
        customTabsIntent.intent.addFlags(FLAG_ACTIVITY_NO_HISTORY)
        return try {
            customTabsIntent.launchUrl(this, website.uri)
            true
        } catch (ex: ActivityNotFoundException) {
            false
        }
    }

    override fun openMarket(packageName: String) {
        try {
            startActivity(Intent(ACTION_VIEW, Uri.parse(MARKET_URI + packageName)))
        } catch (ex: ActivityNotFoundException) {
            startActivity(Intent(ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URI + packageName)))
        }
    }

    override fun openLegacyLoginScreen() {
        startActivityForResult(createLoginIntent(), LOGIN_REQUEST_CODE)
    }

    override fun close() {
        finish()
    }

    private fun Uri.findParameter(key: String): String? =
        if (
            scheme == LOGIN_CALLBACK_URI.scheme
            && host == LOGIN_CALLBACK_URI.host
            && path == LOGIN_CALLBACK_URI.path
        ) {
            getQueryParameter(key)
        } else {
            null
        }

}