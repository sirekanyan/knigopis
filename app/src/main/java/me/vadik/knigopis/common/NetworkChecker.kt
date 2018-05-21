package me.vadik.knigopis.common

import android.app.Application
import me.vadik.knigopis.utils.systemConnectivityManager

interface NetworkChecker {

    fun isNetworkAvailable(): Boolean

}

class NetworkCheckerImpl(app: Application) : NetworkChecker {

    private val connectivityManager = app.systemConnectivityManager

    override fun isNetworkAvailable(): Boolean =
        connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false

}