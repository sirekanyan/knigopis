package com.sirekanyan.knigopis.dependency

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sirekanyan.knigopis.App
import com.sirekanyan.knigopis.BuildConfig
import com.sirekanyan.knigopis.DATE_FORMAT
import com.sirekanyan.knigopis.MAIN_API
import com.sirekanyan.knigopis.common.android.NetworkChecker
import com.sirekanyan.knigopis.common.android.NetworkCheckerImpl
import com.sirekanyan.knigopis.common.android.ResourceProvider
import com.sirekanyan.knigopis.common.android.ResourceProviderImpl
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookHeaderModel
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.repository.*
import com.sirekanyan.knigopis.repository.cache.CommonCache
import com.sirekanyan.knigopis.repository.cache.CommonCacheImpl
import com.sirekanyan.knigopis.repository.cache.HeadedModelDeserializer
import com.sirekanyan.knigopis.repository.network.AuthInterceptor
import com.sirekanyan.knigopis.repository.network.CookieStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

fun App.provideConfig(): Configuration =
    ConfigurationImpl(this)

fun App.provideResources(): ResourceProvider =
    ResourceProviderImpl(this)

fun App.provideTokenStorage(): TokenStorage =
    TokenStorageImpl(this)

fun App.provideAuthRepository(): AuthRepository =
    AuthRepositoryImpl(endpoint, tokenStorage)

fun App.provideBookRepository(): BookRepository {
    val plannedOrganizer = PlannedBookOrganizerImpl(resourceProvider, config)
    val finishedOrganizer = FinishedBookOrganizerImpl(resourceProvider, config)
    return BookRepositoryImpl(endpoint, cache, plannedOrganizer, finishedOrganizer, networkChecker)
}

fun App.provideUserRepository(): UserRepository {
    val organizer = UserOrganizer(config)
    return UserRepositoryImpl(endpoint, cache, organizer, networkChecker)
}

fun App.provideNoteRepository(): NoteRepository =
    NoteRepositoryImpl(endpoint, cache, networkChecker)

fun App.provideNetworkChecker(): NetworkChecker =
    NetworkCheckerImpl(this)

fun App.provideEndpoint(): Endpoint =
    Retrofit.Builder()
        .baseUrl(MAIN_API)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(
            OkHttpClient.Builder()
                .cookieJar(CookieStorage)
                .addInterceptor(AuthInterceptor(tokenStorage))
                .setDebugEnabled(BuildConfig.DEBUG)
                .build()
        )
        .build()
        .create(Endpoint::class.java)

fun App.provideCache(): CommonCache =
    CommonCacheImpl(this, gson)

fun provideGson(): Gson =
    GsonBuilder().registerTypeAdapter(
        BookModel::class.java,
        HeadedModelDeserializer<BookModel>(
            BookHeaderModel::class.java,
            BookDataModel::class.java
        )
    )
        .setDateFormat(DATE_FORMAT)
        .create()

private fun OkHttpClient.Builder.setDebugEnabled(debugEnabled: Boolean) =
    apply {
        if (debugEnabled) {
            addNetworkInterceptor(HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }