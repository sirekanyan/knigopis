package me.vadik.knigopis.common

import android.app.Application
import me.vadik.knigopis.common.extensions.systemConnectivityManager

interface NetworkChecker {

    fun isNetworkAvailable(): Boolean

}

class NetworkCheckerImpl(app: Application) : NetworkChecker {

    private val connectivityManager = app.systemConnectivityManager

    override fun isNetworkAvailable(): Boolean =
        connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false

}