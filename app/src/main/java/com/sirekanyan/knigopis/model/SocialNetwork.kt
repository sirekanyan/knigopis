package com.sirekanyan.knigopis.model

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sirekanyan.knigopis.R

fun Uri.toSocialNetwork() = SocialNetwork.values().find { host in it.hosts }

enum class SocialNetwork(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    vararg val hosts: String
) {
    FACEBOOK(R.string.common_social_facebook, R.drawable.ic_social_facebook, "www.facebook.com"),
    INSTAGRAM(R.string.common_social_instagram, R.drawable.ic_social_instagram, "instagram.com", "www.instagram.com"),
    TWITTER(R.string.common_social_twitter, R.drawable.ic_social_twitter, "twitter.com"),
    TELEGRAM(R.string.common_social_telegram, R.drawable.ic_social_telegram, "t.me"),
    GOOGLE(R.string.common_social_googleplus, R.drawable.ic_social_googleplus, "plus.google.com"),
    VK_COM(R.string.common_social_vkontakte, R.drawable.ic_social_vkontakte, "vk.com"),
    MAIL_RU(R.string.common_social_mailru, R.drawable.ic_social_mailru, "my.mail.ru"),
    LINKEDIN(R.string.common_social_linkedin, R.drawable.ic_social_linkedin, "www.linkedin.com")
}