package com.sirekanyan.knigopis.repository.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.atomic.AtomicReference

object CookieStorage : CookieJar {

    private val cookies = AtomicReference<List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies.set(cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies.get() ?: listOf()
    }

}