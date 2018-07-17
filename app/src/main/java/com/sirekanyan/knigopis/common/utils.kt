package com.sirekanyan.knigopis.common

import android.animation.ObjectAnimator
import android.app.Activity
import android.net.Uri
import android.support.annotation.DimenRes
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.sirekanyan.knigopis.App
import com.sirekanyan.knigopis.BuildConfig.APPLICATION_ID
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val TAG = "Knigopis"
private val HTTP_SCHEMES = setOf("http", "https")

fun extra(name: String) = "$APPLICATION_ID.extra_$name"

fun Activity.app() = application as App

@Suppress("unused")
fun logWarn(message: String) = Log.w(TAG, message)

fun logError(message: String, throwable: Throwable?) = Log.e(TAG, message, throwable)

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

fun View.showScale() {
    alpha = 0f
    scaleX = 0f
    scaleY = 0f
    animate().alpha(1f).setDuration(200)
        .scaleX(1f).scaleY(1f)
        .setInterpolator(LinearOutSlowInInterpolator())
        .withStartAction { visibility = View.VISIBLE }
}

fun View.hideScale() {
    animate().alpha(0f).setDuration(200)
        .scaleX(0f).scaleY(0f)
        .setInterpolator(FastOutLinearInInterpolator())
        .withEndAction { visibility = View.GONE }
}

val View.isVisible get() = visibility == View.VISIBLE

fun ProgressBar.setProgressSmoothly(progress: Int) {
    ObjectAnimator.ofInt(this, "progress", progress).start()
}

fun String.toUriOrNull() =
    Uri.parse(this).takeIf(Uri::isValidHttpLink)

private fun Uri.isValidHttpLink() =
    scheme in HTTP_SCHEMES && !host.isNullOrBlank()