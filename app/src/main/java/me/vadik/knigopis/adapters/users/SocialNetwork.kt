package me.vadik.knigopis.adapters.users

import android.net.Uri
import android.support.annotation.StringRes
import me.vadik.knigopis.R

fun Uri.toSocialNetwork() = SocialNetwork.values().find { it.host == host }

enum class SocialNetwork(@StringRes val titleRes: Int, val host: String) {
    FACEBOOK(R.string.social_facebook, "www.facebook.com"),
    INSTAGRAM(R.string.social_instagram, "www.instagram.com"),
    TWITTER(R.string.social_twitter, "twitter.com"),
    GOOGLE(R.string.social_google_plus, "plus.google.com"),
    VK_COM(R.string.social_vk_com, "vk.com"),
    MAIL_RU(R.string.social_mail_ru, "my.mail.ru");
}