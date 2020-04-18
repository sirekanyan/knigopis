package com.sirekanyan.knigopis.feature.login

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.setDarkTheme
import com.sirekanyan.knigopis.dependency.providePresenter
import com.sirekanyan.knigopis.feature.startMainActivity

private const val MARKET_URI = "market://details?id="
private const val GOOGLE_PLAY_URI = "https://play.google.com/store/apps/details?id="

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

    override fun openBrowser(website: Website): Boolean {
        val toolbarColor = ContextCompat.getColor(this, website.color)
        val customTabsIntent = CustomTabsIntent.Builder().setToolbarColor(toolbarColor).build()
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