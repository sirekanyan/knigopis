package com.sirekanyan.knigopis

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sirekanyan.knigopis.common.android.*
import com.sirekanyan.knigopis.common.android.dialog.BottomSheetDialogFactory
import com.sirekanyan.knigopis.common.android.dialog.DialogFactory
import com.sirekanyan.knigopis.common.extensions.getRootView
import com.sirekanyan.knigopis.feature.MainPresenter
import com.sirekanyan.knigopis.feature.MainPresenterImpl
import com.sirekanyan.knigopis.feature.MainViewImpl
import com.sirekanyan.knigopis.feature.ProgressViewImpl
import com.sirekanyan.knigopis.feature.login.LoginPresenterImpl
import com.sirekanyan.knigopis.feature.login.LoginViewImpl
import com.sirekanyan.knigopis.feature.notes.NotesPresenterImpl
import com.sirekanyan.knigopis.feature.notes.NotesViewImpl
import com.sirekanyan.knigopis.feature.user.UserInteractor
import com.sirekanyan.knigopis.feature.user.UserInteractorImpl
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookHeaderModel
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.dto.FinishedBook
import com.sirekanyan.knigopis.model.dto.PlannedBook
import com.sirekanyan.knigopis.repository.*
import com.sirekanyan.knigopis.repository.cache.CommonCache
import com.sirekanyan.knigopis.repository.cache.CommonCacheImpl
import com.sirekanyan.knigopis.repository.cache.HeadedModelDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.parameter.Parameters
import org.koin.dsl.context.ParameterProvider
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.dsl.context.Context as KoinContext

private const val MAIN_API_URL = "http://api.knigopis.com"
private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
private const val CONTEXT_KEY = "context_key"
private const val ROOT_VIEW_KEY = "root_view_key"
private const val ROUTER_KEY = "router_key"

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
    bean { AuthRepositoryImpl(get(), get()) as AuthRepository }
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
    factory { BottomSheetDialogFactory(it.getContext()) as DialogFactory }
    factory { PermissionsImpl(it.getContext() as Activity) as Permissions }
    mainModule()
    userModule()
}

private fun KoinContext.mainModule() {
    factory {
        val params = it.getContext().createParameters()
        val progressView = ProgressViewImpl(it.getRootView(R.id.swipeRefresh))
        val loginPresenter = LoginPresenterImpl(it.getRouter(), get(params))
        val notesPresenter = NotesPresenterImpl(get())
        MainPresenterImpl(
            loginPresenter,
            notesPresenter,
            it.getRouter(),
            get(),
            get(),
            get(),
            get(),
            get(),
            progressView // TODO: remove
        ).also { p ->
            loginPresenter.view = LoginViewImpl(it.getRootView(), loginPresenter)
            notesPresenter.view = NotesViewImpl(it.getRootView(R.id.notesPage), p, progressView)
            p.view = MainViewImpl(it.getRootView(), p, get(params))
        } as MainPresenter
    }
}

private fun KoinContext.userModule() {
    bean { UserInteractorImpl(get(), get(), get()) as UserInteractor }
}

fun Context.createParameters(): Parameters =
    { mapOf(CONTEXT_KEY to this) }

fun Activity.createParameters(router: MainPresenter.Router): Parameters =
    {
        mapOf(
            CONTEXT_KEY to this,
            ROOT_VIEW_KEY to getRootView(),
            ROUTER_KEY to router
        )
    }

private fun ParameterProvider.getContext(): Context =
    this[CONTEXT_KEY]

private fun ParameterProvider.getRootView(): View =
    this[ROOT_VIEW_KEY]

private fun ParameterProvider.getRootView(id: Int): View =
    getRootView().findViewById(id)

private fun <T> ParameterProvider.getRouter(): T =
    this[ROUTER_KEY]

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