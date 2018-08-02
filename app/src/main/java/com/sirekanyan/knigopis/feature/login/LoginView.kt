package com.sirekanyan.knigopis.feature.login

import android.support.v7.app.AlertDialog
import android.view.View
import com.sirekanyan.knigopis.R
import kotlinx.android.extensions.LayoutContainer

interface LoginView {

    fun showPermissionsRetryDialog()
    fun showPermissionsSettingsDialog()

    interface Callbacks {
        fun onRetryLoginClicked()
        fun onGotoSettingsClicked()
    }

}

class LoginViewImpl(
    override val containerView: View,
    private val callbacks: LoginView.Callbacks
) : LoginView,
    LayoutContainer {

    private val context = containerView.context

    override fun showPermissionsRetryDialog() {
        AlertDialog.Builder(context)
            .setTitle(R.string.permissions_title)
            .setMessage(R.string.permissions_message_retry)
            .setPositiveButton(R.string.common_button_retry) { _, _ ->
                callbacks.onRetryLoginClicked()
            }
            .setNegativeButton(R.string.common_button_cancel, null)
            .setCancelable(false)
            .show()
    }

    override fun showPermissionsSettingsDialog() {
        AlertDialog.Builder(context)
            .setTitle(R.string.permissions_title)
            .setMessage(R.string.permissions_message_settings)
            .setPositiveButton(R.string.permissions_button_settings) { _, _ ->
                callbacks.onGotoSettingsClicked()
            }
            .setNegativeButton(R.string.common_button_cancel, null)
            .setCancelable(false)
            .show()
    }

}