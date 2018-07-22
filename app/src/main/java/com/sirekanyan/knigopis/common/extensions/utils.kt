package com.sirekanyan.knigopis.common.extensions

import android.app.Activity
import android.net.Uri
import android.support.annotation.DimenRes
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.util.Log
import android.view.View
import com.sirekanyan.knigopis.App
import com.sirekanyan.knigopis.BuildConfig.APPLICATION_ID
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private val HTTP_SCHEMES = setOf("http", "https")

fun Activity.app() = application as App

fun <T> Single<T>.io2main(): Single<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.io2main(): Flowable<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.io2main(): Completable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun String.orDefault(default: String) = if (isEmpty()) default else this

fun View.setElevationRes(@DimenRes elevation: Int) {
    ViewCompat.setElevation(this, resources.getDimensionPixelSize(elevation).toFloat())
}

fun View.show(value: Boolean) {
    if (value) show() else hide()
}

fun View.show() {
    animate().alpha(1f).setDuration(200)
        .withStartAction { visibility = View.VISIBLE }
}

fun View.hide() {
    animate().alpha(0f).setDuration(200)
        .withEndAction { visibility = View.GONE }
}

fun View.startExpandAnimation() {
    alpha = 0f
    scaleX = 0f
    scaleY = 0f
    animate().alpha(1f).setDuration(200)
        .setInterpolator(LinearOutSlowInInterpolator())
        .scaleX(1f).scaleY(1f)
}

fun View.startCollapseAnimation() {
    animate().alpha(0f).setDuration(200)
        .setInterpolator(FastOutLinearInInterpolator())
        .scaleX(0f).scaleY(0f)
}

val View.isVisible get() = visibility == View.VISIBLE

fun String.toUriOrNull() =
    Uri.parse(this).takeIf(Uri::isValidHttpLink)

private fun Uri.isValidHttpLink() =
    scheme in HTTP_SCHEMES && !host.isNullOrBlank()