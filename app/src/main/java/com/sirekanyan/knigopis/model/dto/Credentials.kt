package com.sirekanyan.knigopis.model.dto

import com.google.gson.annotations.SerializedName

class Credentials(
    @SerializedName("access-token")
    val accessToken: String,
    val user: User
)