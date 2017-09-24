package me.vadik.knigopis

import io.reactivex.Single
import me.vadik.knigopis.model.ImageThumbnail
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageEndpoint {

  @GET("search/images?count=1")
  fun searchImage(@Query("q") query: String): Single<ImageThumbnail>
}