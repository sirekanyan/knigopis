package me.vadik.knigopis.model

import com.google.gson.annotations.SerializedName

class Credentials(
    @SerializedName("access-token")
    val accessToken: String,
    val user: UserFull
) {

    class UserFull(
        val id: String,
        val lang: String,
        val nickname: String,
        val photo: String,
        val profile: String,
        val identity: String,
        val booksCount: Int,
        val subscriptions: Map<String, Int>,
        val createdAt: String,
        val updatedAt: String
    )
}