package me.vadik.knigopis.api

import io.reactivex.Single
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.Credentials
import me.vadik.knigopis.model.User
import me.vadik.knigopis.model.PlannedBook
import retrofit2.http.GET
import retrofit2.http.Query

interface Endpoint {

  @GET("user/get-credentials")
  fun getCredentials(@Query("token") token: String): Single<Credentials>

  @GET("books")
  fun getFinishedBooks(@Query("access-token") accessToken: String): Single<List<FinishedBook>>

  @GET("wishes")
  fun getPlannedBooks(@Query("access-token") accessToken: String): Single<List<PlannedBook>>

  @GET("users/latest")
  fun getLatestUsers(): Single<Map<String, User>>

  @GET("books/latest-notes")
  fun getLatestBooksWithNotes(): Single<Map<String, FinishedBook>>
}