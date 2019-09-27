package com.sirekanyan.knigopis.feature.login

import com.sirekanyan.knigopis.R

enum class Website(private val code: String, val color: Int, val title: Int, val icon: Int) {

    VK("vkontakte", R.color.social_vk, R.string.login_website_vk, R.drawable.ic_login_vk),
    GO("google", R.color.social_go, R.string.login_website_go, R.drawable.ic_login_go),
    FB("facebook", R.color.social_fb, R.string.login_website_fb, R.drawable.ic_login_fb),
    MR("mailru", R.color.social_mr, R.string.login_website_mr, R.drawable.ic_login_mr),
    YA("yandex", R.color.social_ya, R.string.login_website_ya, R.drawable.ic_login_ya),
    TW("twitter", R.color.social_tw, R.string.login_website_tw, R.drawable.ic_login_tw),
    IN("instagram", R.color.social_in, R.string.login_website_in, R.drawable.ic_login_in);

    val uri get() = buildLoginUri(code)

}