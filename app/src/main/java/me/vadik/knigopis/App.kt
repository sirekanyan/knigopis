package me.vadik.knigopis

import android.app.Application
import com.google.gson.GsonBuilder
import me.vadik.knigopis.api.gson.ImageThumbnailDeserializer
import me.vadik.knigopis.model.ImageThumbnail
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val MAIN_API_URL = "http://api.knigopis.com"
private const val IMAGE_API_URL = "https://api.qwant.com/api/"

class App : Application() {

    val baseApi: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(MAIN_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .setDebugEnabled(BuildConfig.DEBUG)
                    .build()
            )
            .build()
    }

    val imageApi: Retrofit by lazy {
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
    }

    private fun OkHttpClient.Builder.setDebugEnabled(debugEnabled: Boolean): OkHttpClient.Builder {
        if (debugEnabled) {
            addNetworkInterceptor(HttpLoggingInterceptor().also {
                it.level = Level.BODY
            })
        }
        return this
    }
}