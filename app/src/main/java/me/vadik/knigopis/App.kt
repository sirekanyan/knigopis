package me.vadik.knigopis

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

  val retrofit by lazy {
    Retrofit.Builder()
        .baseUrl("http://api.knigopis.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
  }
}