package com.sirekanyan.knigopis.repository.model

private val defaultAvatars = setOf(
    "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=100",
    "https://lh6.googleusercontent.com/-cfU0I0DdeGE/AAAAAAAAAAI/AAAAAAAAANs/RoQmKyJjwLo/photo.jpg?sz=100",
    "https://vk.com/images/camera_50.png",
    "http://vk.com/images/camera_50.png",
    "https://ulogin.ru/img/photo.png",
    "http://avt-27.foto.mail.ru/mail/newmoon56/_avatar"
)

class User(
    val id: String,
    val nickname: String?,
    val photo: String?,
    private val profile: String?,
    private val identity: String?,
    val booksCount: Int
) {

    val fixedProfile get() = "http://www.knigopis.com/#/user/books?u=$id"

    val name get() = nickname ?: id

    val avatar get() = photo.takeUnless { it in defaultAvatars }

    val profiles get() = listOfNotNull(profile, identity)

}