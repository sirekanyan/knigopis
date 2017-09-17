package me.vadik.knigopis

import retrofit2.Call
import retrofit2.http.GET

interface Endpoint {
  @GET("users/latest")
  fun latestUsers(): Call<Map<String, User>>
}