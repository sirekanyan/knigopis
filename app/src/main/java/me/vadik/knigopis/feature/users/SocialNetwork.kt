package me.vadik.knigopis.feature.users

import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import me.vadik.knigopis.R

fun Uri.toSocialNetwork() = SocialNetwork.values().find { it.host == host }

enum class SocialNetwork(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val host: String
) {
    FACEBOOK(R.string.common_social_facebook, R.drawable.ic_social_facebook, "www.facebook.com"),
    INSTAGRAM(R.string.common_social_instagram, R.drawable.ic_social_instagram, "www.instagram.com"),
    TWITTER(R.string.common_social_twitter, R.drawable.ic_social_twitter, "twitter.com"),
    GOOGLE(R.string.common_social_googleplus, R.drawable.ic_social_googleplus, "plus.google.com"),
    VK_COM(R.string.common_social_vkontakte, R.drawable.ic_social_vkontakte, "vk.com"),
    MAIL_RU(R.string.common_social_mailru, R.drawable.ic_social_mailru, "my.mail.ru"),
    LINKEDIN(R.string.common_social_linkedin, R.drawable.ic_social_linkedin, "www.linkedin.com")
}