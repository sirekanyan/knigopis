package me.vadik.knigopis

import android.app.Application
import me.vadik.knigopis.dependency.appModule
import org.koin.android.ext.android.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}