package com.sirekanyan.knigopis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sirekanyan.knigopis.common.NetworkChecker
import com.sirekanyan.knigopis.common.NetworkCheckerImpl
import com.sirekanyan.knigopis.common.ResourceProvider
import com.sirekanyan.knigopis.common.ResourceProviderImpl
import com.sirekanyan.knigopis.common.view.dialog.BottomSheetDialogFactory
import com.sirekanyan.knigopis.common.view.dialog.DialogFactory
import com.sirekanyan.knigopis.feature.user.UserInteractor
import com.sirekanyan.knigopis.feature.user.UserInteractorImpl
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookHeaderModel
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.dto.FinishedBook
import com.sirekanyan.knigopis.model.dto.PlannedBook
import com.sirekanyan.knigopis.repository.*
import com.sirekanyan.knigopis.repository.Endpoint
import com.sirekanyan.knigopis.repository.cache.CommonCache
import com.sirekanyan.knigopis.repository.cache.CommonCacheImpl
import com.sirekanyan.knigopis.repository.cache.HeadedModelDeserializer
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
    bean { UserRepositoryImpl(get(), get(), get(), get()) as UserRepository }
    bean { NoteRepositoryImpl(get(), get(), get()) as NoteRepository }
    bean { KAuthImpl(get(), get()) as KAuth }
    bean { createMainEndpoint(get()) }
    bean("planned") { PlannedBookOrganizerImpl(get(), get()) as BookOrganizer<PlannedBook> }
    bean("finished") { FinishedBookPrepareImpl(get()) as BookOrganizer<FinishedBook> }
    bean { ConfigurationImpl(get()) as Configuration }
    bean { ResourceProviderImpl(get()) as ResourceProvider }
    bean { NetworkCheckerImpl(get()) as NetworkChecker }
    bean { CommonCacheImpl(get(), get()) as CommonCache }
    bean {
        GsonBuilder().registerTypeAdapter(
            BookModel::class.java,
            HeadedModelDeserializer<BookModel>(
                BookHeaderModel::class.java,
                BookDataModel::class.java
            )
        )
            .setDateFormat(DATE_FORMAT)
            .create()
    }
    factory { BottomSheetDialogFactory(it["activity"]) as DialogFactory }
    userModule()
}

private fun Context.userModule() {
    bean { UserInteractorImpl(get(), get(), get()) as UserInteractor }
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