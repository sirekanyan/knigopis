package me.vadik.knigopis.api

import io.reactivex.Completable
import io.reactivex.Single
import me.vadik.knigopis.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Endpoint {

  @GET("user/get-credentials")
  fun getCredentials(@Query("token") token: String): Single<Credentials>

  @GET("books")
  fun getFinishedBooks(@Query("access-token") accessToken: String): Single<List<FinishedBook>>

  @POST("books")
  fun postFinishedBook(
      @Query("access-token") accessToken: String,
      @Body book: FinishedBookToSend
  ): Completable

  @GET("wishes")
  fun getPlannedBooks(@Query("access-token") accessToken: String): Single<List<PlannedBook>>

  @POST("wishes")
  fun postPlannedBook(
      @Query("access-token") accessToken: String,
      @Body book: PlannedBookToSend
  ): Completable

  @GET("users/latest")
  fun getLatestUsers(): Single<Map<String, User>>

  @GET("books/latest-notes")
  fun getLatestBooksWithNotes(): Single<Map<String, FinishedBook>>
}