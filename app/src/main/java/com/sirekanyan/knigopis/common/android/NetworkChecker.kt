package com.sirekanyan.knigopis.common.android

import android.app.Application
import com.sirekanyan.knigopis.common.extensions.systemConnectivityManager

interface NetworkChecker {

    fun isNetworkAvailable(): Boolean

}

class NetworkCheckerImpl(app: Application) : NetworkChecker {

    private val connectivityManager = app.systemConnectivityManager

    @Suppress("DEPRECATION")
    override fun isNetworkAvailable(): Boolean =
        connectivityManager.activeNetworkInfo?.isConnected ?: false

}