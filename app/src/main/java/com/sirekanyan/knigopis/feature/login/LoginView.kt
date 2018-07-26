package com.sirekanyan.knigopis.feature.login

interface LoginView {

    fun showPermissionsRetryDialog()
    fun showPermissionsSettingsDialog()

    interface Callbacks {
        fun onRetryLoginClicked()
        fun onGotoSettingsClicked()
    }

}