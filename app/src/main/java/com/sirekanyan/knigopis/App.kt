package com.sirekanyan.knigopis

import android.app.Application
import android.content.Context
import com.sirekanyan.knigopis.dependency.*
import org.acra.ACRA
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.HttpSenderConfigurationBuilder
import org.acra.sender.HttpSender

class App : Application() {

    val config by lazy(::provideConfig)
    val resourceProvider by lazy(::provideResources)
    val tokenStorage by lazy(::provideTokenStorage)
    val authRepository by lazy(::provideAuthRepository)
    val bookRepository by lazy(::provideBookRepository)
    val userRepository by lazy(::provideUserRepository)
    val noteRepository by lazy(::provideNoteRepository)
    val networkChecker by lazy(::provideNetworkChecker)
    val endpoint by lazy(::provideEndpoint)
    val cache by lazy(::provideCache)
    val gson by lazy(::provideGson)

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initCrashReporting()
    }

    private fun initCrashReporting() {
        val builder = CoreConfigurationBuilder(this)
            .setBuildConfigClass(BuildConfig::class.java)
        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder::class.java)
            .setUri("https://collector.tracepot.com/93c9aa62")
            .setHttpMethod(HttpSender.Method.POST)
            .setEnabled(true)
        ACRA.init(this, builder)
    }

    override fun onCreate() {
        super.onCreate()
        config.theme.setup()
    }

}