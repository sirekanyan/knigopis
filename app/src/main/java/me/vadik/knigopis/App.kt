package me.vadik.knigopis

import android.app.Application
import android.content.Context
import me.vadik.knigopis.dependency.AppComponent
import me.vadik.knigopis.dependency.DaggerAppComponent

val Context.app get() = applicationContext as App

class App : Application() {
    val component: AppComponent = DaggerAppComponent.create()
}