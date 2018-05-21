package me.vadik.knigopis.repository.api

import io.reactivex.Single
import me.vadik.knigopis.repository.model.ImageThumbnail
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageEndpoint {

    @GET("search/images")
    fun searchImage(
        @Query("q") query: String,
        @Query("count") count: Int = 10
    ): Single<ImageThumbnail>
}