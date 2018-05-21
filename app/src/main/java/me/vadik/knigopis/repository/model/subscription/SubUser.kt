package me.vadik.knigopis.repository.model.subscription

import android.net.Uri
import me.vadik.knigopis.common.toUriOrNull

private val defaultAvatars = setOf(
    "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=100",
    "https://lh6.googleusercontent.com/-cfU0I0DdeGE/AAAAAAAAAAI/AAAAAAAAANs/RoQmKyJjwLo/photo.jpg?sz=100",
    "https://vk.com/images/camera_50.png",
    "http://vk.com/images/camera_50.png",
    "https://ulogin.ru/img/photo.png",
    "http://avt-27.foto.mail.ru/mail/newmoon56/_avatar"
)

class SubUser(
    val id: String,
    private val nickname: String?,
    private val photo: String?,
    private val profile: String?,
    private val identity: String?,
    val booksCount: Int
) {

    val name get() = nickname ?: id

    val profiles: List<Uri>
        get() = listOfNotNull(profile, identity)
            .mapNotNull(String::toUriOrNull)

    val avatar: String?
        get() = photo.takeUnless { it in defaultAvatars }

}
