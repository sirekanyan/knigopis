package me.vadik.knigopis.dependency

import com.google.gson.GsonBuilder
import me.vadik.knigopis.*
import me.vadik.knigopis.api.BookCoverSearch
import me.vadik.knigopis.api.BookCoverSearchImpl
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.api.ImageEndpoint
import me.vadik.knigopis.api.gson.ImageThumbnailDeserializer
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.auth.KAuthImpl
import me.vadik.knigopis.common.ResourceProvider
import me.vadik.knigopis.common.ResourceProviderImpl
import me.vadik.knigopis.data.BookOrganizer
import me.vadik.knigopis.data.FinishedBookPrepareImpl
import me.vadik.knigopis.data.PlannedBookOrganizerImpl
import me.vadik.knigopis.dialog.BottomSheetDialogFactory
import me.vadik.knigopis.dialog.DialogFactory
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.ImageThumbnail
import me.vadik.knigopis.model.PlannedBook
import me.vadik.knigopis.user.UserInteractor
import me.vadik.knigopis.user.UserInteractorImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.context.Context
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val MAIN_API_URL = "http://api.knigopis.com"
private const val IMAGE_API_URL = "https://api.qwant.com/api/"
private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

val appModule = applicationContext {
    bean { BookRepositoryImpl(get(), get(), get("planned"), get("finished")) as BookRepository }
    bean { BookCoverSearchImpl(get(), BookCoverCacheImpl(get())) as BookCoverSearch }
    bean { KAuthImpl(get(), get()) as KAuth }
    bean { createMainEndpoint() }
    bean { createImageEndpoint() }
    bean("planned") { PlannedBookOrganizerImpl(get(), get()) as BookOrganizer<PlannedBook> }
    bean("finished") { FinishedBookPrepareImpl(get()) as BookOrganizer<FinishedBook> }
    bean { ConfigurationImpl(get()) as Configuration }
    bean { ResourceProviderImpl(get()) as ResourceProvider }
    factory { BottomSheetDialogFactory(it["activity"]) as DialogFactory }
    userModule()
}

private fun Context.userModule() {
    bean { UserInteractorImpl(get(), get()) as UserInteractor }
}

private fun createMainEndpoint() =
    Retrofit.Builder()
        .baseUrl(MAIN_API_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setDateFormat(DATE_FORMAT).create()
            )
        )
        .client(
            OkHttpClient.Builder()
                .setDebugEnabled(BuildConfig.DEBUG)
                .build()
        )
        .build()
        .create(Endpoint::class.java)

private fun createImageEndpoint() =
    Retrofit.Builder()
        .baseUrl(IMAGE_API_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().registerTypeAdapter(
                    ImageThumbnail::class.java,
                    ImageThumbnailDeserializer()
                ).create()
            )
        )
        .build()
        .create(ImageEndpoint::class.java)

private fun OkHttpClient.Builder.setDebugEnabled(debugEnabled: Boolean) =
    apply {
        if (debugEnabled) {
            addNetworkInterceptor(HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }