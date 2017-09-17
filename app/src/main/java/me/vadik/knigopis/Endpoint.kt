package me.vadik.knigopis

import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.User
import retrofit2.Call
import retrofit2.http.GET

interface Endpoint {

  @GET("users/latest")
  fun latestUsers(): Call<Map<String, User>>

  @GET("books/latest-notes")
  fun latestBooksWithNotes(): Call<Map<String, Book>>
}