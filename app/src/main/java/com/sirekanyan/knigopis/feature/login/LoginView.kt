package com.sirekanyan.knigopis.feature.login

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.extensions.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.default_app_bar.*
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.website_layout.view.*

interface LoginView {

    fun addWebsite(website: Website)
    fun showNoBrowserDialog()

    interface Callbacks {

        fun onWebsiteClicked(website: Website)
        fun onInstallBrowserClicked(packageName: String)
        fun onBackClicked()

    }

}

class LoginViewImpl(
    override val containerView: View,
    private val callbacks: LoginView.Callbacks
) : LoginView,
    LayoutContainer {

    private val context = containerView.context

    init {
        toolbar.setTitle(R.string.login_title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { callbacks.onBackClicked() }
    }

    override fun addWebsite(website: Website) {
        websitesContainer.addView(
            websitesContainer.inflate<ViewGroup>(R.layout.website_layout).also { container ->
                container.websiteTitle.setText(website.title)
                container.websiteLogo.setImageResource(website.icon)
                container.setOnClickListener {
                    callbacks.onWebsiteClicked(website)
                }
            }
        )
    }

    override fun showNoBrowserDialog() {
        AlertDialog.Builder(context)
            .setTitle(R.string.login_browser_title)
            .setItems(R.array.login_browsers) { _, which ->
                when (which) {
                    0 -> callbacks.onInstallBrowserClicked("org.mozilla.firefox")
                    1 -> callbacks.onInstallBrowserClicked("com.android.chrome")
                }
            }
            .show()
    }

}