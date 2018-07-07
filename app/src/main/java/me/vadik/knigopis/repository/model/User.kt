package me.vadik.knigopis.repository.model

import java.util.*

class User(
    val id: String,
    val lang: String,
    val nickname: String?,
    val photo: String,
    val profile: String,
    val identity: String,
    val booksCount: Int,
    val subscriptions: Map<String, Int>?,
    val createdAt: Date,
    val updatedAt: Date
) {
    // TODO https://trello.com/c/UymHYoPK
    val fixedCreatedAt get() = Date(createdAt.time + TimeZone.getDefault().rawOffset)
    val fixedUpdatedAt get() = Date(updatedAt.time + TimeZone.getDefault().rawOffset)
    val fixedProfile get() = "http://www.knigopis.com/#/user/books?u=$id"
}