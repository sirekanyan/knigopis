package com.sirekanyan.knigopis

import android.app.Application
import com.sirekanyan.knigopis.dependency.*

class App : Application() {
    val config by lazy(::provideConfig)
    val resourceProvider by lazy(::provideResources)
    val authRepository by lazy(::provideAuthRepository)
    val bookRepository by lazy(::provideBookRepository)
    val userRepository by lazy(::provideUserRepository)
    val noteRepository by lazy(::provideNoteRepository)
    val networkChecker by lazy(::provideNetworkChecker)
    val endpoint by lazy(::provideEndpoint)
    val cache by lazy(::provideCache)
    val gson by lazy(::provideGson)
}