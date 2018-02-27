package me.vadik.knigopis.dependency

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import me.vadik.knigopis.BuildConfig
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.api.ImageEndpoint
import me.vadik.knigopis.api.gson.ImageThumbnailDeserializer
import me.vadik.knigopis.model.ImageThumbnail
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val MAIN_API_URL = "http://api.knigopis.com"
private const val IMAGE_API_URL = "https://api.qwant.com/api/"
private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideBaseApi(): Endpoint =
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

    @Provides
    @Singleton
    fun provideImageApi(): ImageEndpoint =
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
}

private fun OkHttpClient.Builder.setDebugEnabled(debugEnabled: Boolean): OkHttpClient.Builder {
    if (debugEnabled) {
        addNetworkInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        })
    }
    return this
}