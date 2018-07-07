package me.vadik.knigopis.repository.model

import com.google.gson.annotations.SerializedName

class Credentials(
    @SerializedName("access-token")
    val accessToken: String,
    val user: User
)