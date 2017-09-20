package me.vadik.knigopis

import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.Credentials
import me.vadik.knigopis.model.User
import me.vadik.knigopis.model.Wish
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Endpoint {

  @GET("/user/get-credentials")
  fun getCredentials(@Query("token") token: String): Call<Credentials>

  @GET("books")
  fun getBooks(@Query("access-token") accessToken: String): Call<List<Book>>

  @GET("wishes")
  fun getWishes(@Query("access-token") accessToken: String): Call<List<Wish>>

  @GET("users/latest")
  fun getLatestUsers(): Call<Map<String, User>>

  @GET("books/latest-notes")
  fun getLatestBooksWithNotes(): Call<Map<String, Book>>
}