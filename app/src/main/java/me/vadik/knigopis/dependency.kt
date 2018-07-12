package me.vadik.knigopis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.vadik.knigopis.common.NetworkChecker
import me.vadik.knigopis.common.NetworkCheckerImpl
import me.vadik.knigopis.common.ResourceProvider
import me.vadik.knigopis.common.ResourceProviderImpl
import me.vadik.knigopis.common.view.dialog.BottomSheetDialogFactory
import me.vadik.knigopis.common.view.dialog.DialogFactory
import me.vadik.knigopis.feature.user.UserInteractor
import me.vadik.knigopis.feature.user.UserInteractorImpl
import me.vadik.knigopis.repository.*
import me.vadik.knigopis.repository.api.Endpoint
import me.vadik.knigopis.repository.cache.*
import me.vadik.knigopis.repository.cache.common.CommonCache
import me.vadik.knigopis.repository.cache.common.CommonCacheImpl
import me.vadik.knigopis.repository.model.FinishedBook
import me.vadik.knigopis.repository.model.PlannedBook
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.context.Context
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val MAIN_API_URL = "http://api.knigopis.com"
private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

val appModule = applicationContext {
    bean {
        BookRepositoryImpl(
            get(),
            get(),
            get(),
            get("planned"),
            get("finished"),
            get()
        ) as BookRepository
    }
    bean { SubscriptionRepositoryImpl(get(), get(), get(), get()) as SubscriptionRepository }
    bean { NoteRepositoryImpl(get(), get(), get()) as NoteRepository }
    bean { KAuthImpl(get(), get()) as KAuth }
    bean { createMainEndpoint(get()) }
    bean("planned") { PlannedBookOrganizerImpl(get(), get()) as BookOrganizer<PlannedBook> }
    bean("finished") { FinishedBookPrepareImpl(get()) as BookOrganizer<FinishedBook> }
    bean { ConfigurationImpl(get()) as Configuration }
    bean { ResourceProviderImpl(get()) as ResourceProvider }
    bean { NetworkCheckerImpl(get()) as NetworkChecker }
    bean { BookCacheImpl(get()) as BookCache }
    bean { SubscriptionCacheImpl(get()) as SubscriptionCache }
    bean { NoteCacheImpl(get()) as NoteCache }
    bean { CommonCacheImpl(get(), get()) as CommonCache }
    bean { GsonBuilder().setDateFormat(DATE_FORMAT).create() }
    factory { BottomSheetDialogFactory(it["activity"]) as DialogFactory }
    userModule()
}

private fun Context.userModule() {
    bean { UserInteractorImpl(get(), get()) as UserInteractor }
}

private fun createMainEndpoint(gson: Gson) =
    Retrofit.Builder()
        .baseUrl(MAIN_API_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(gson)
        )
        .client(
            OkHttpClient.Builder()
                .setDebugEnabled(BuildConfig.DEBUG)
                .build()
        )
        .build()
        .create(Endpoint::class.java)

private fun OkHttpClient.Builder.setDebugEnabled(debugEnabled: Boolean) =
    apply {
        if (debugEnabled) {
            addNetworkInterceptor(HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }