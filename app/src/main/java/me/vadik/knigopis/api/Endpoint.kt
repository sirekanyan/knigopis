package me.vadik.knigopis.api

import io.reactivex.Completable
import io.reactivex.Single
import me.vadik.knigopis.model.*
import retrofit2.http.*

interface Endpoint {

  @GET("user/get-credentials")
  fun getCredentials(@Query("token") token: String): Single<Credentials>

  @GET("books")
  fun getFinishedBooks(@Query("access-token") accessToken: String): Single<List<FinishedBook>>

  @GET("books/{id}")
  fun getFinishedBook(@Path("id") id: String): Single<FinishedBook>

  @POST("books")
  fun postFinishedBook(
      @Query("access-token") accessToken: String,
      @Body book: FinishedBookToSend
  ): Completable

  @DELETE("books/{id}")
  fun deleteFinishedBook(
      @Path("id") id: String,
      @Query("access-token") accessToken: String
  ): Completable

  @GET("wishes")
  fun getPlannedBooks(@Query("access-token") accessToken: String): Single<List<PlannedBook>>

  @GET("wishes/{id}")
  fun getPlannedBook(@Path("id") id: String): Single<PlannedBook>

  @POST("wishes")
  fun postPlannedBook(
      @Query("access-token") accessToken: String,
      @Body book: PlannedBookToSend
  ): Completable

  @DELETE("wishes/{id}")
  fun deletePlannedBook(
      @Path("id") id: String,
      @Query("access-token") accessToken: String
  ): Completable

  @GET("users/latest")
  fun getLatestUsers(): Single<Map<String, User>>

  @GET("books/latest-notes")
  fun getLatestBooksWithNotes(): Single<Map<String, FinishedBook>>
}