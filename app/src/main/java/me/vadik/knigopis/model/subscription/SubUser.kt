package me.vadik.knigopis.model.subscription

import android.net.Uri
import me.vadik.knigopis.toUriOrNull

class SubUser(
    val id: String,
    val nickname: String,
    val photo: String?,
    private val profile: String?,
    private val identity: String?,
    val booksCount: Int
) {
    val profiles: List<Uri>
        get() = listOfNotNull(profile, identity)
            .mapNotNull(String::toUriOrNull)
}
