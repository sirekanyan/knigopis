package me.vadik.knigopis

import android.app.Activity
import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val TAG = "Knigopis"

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toast(@StringRes messageId: Int) = Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()

fun Activity.app() = application as App

fun <T : View> Activity.findView(@IdRes id: Int): T = findViewById(id)

fun logWarn(message: String) = Log.w(TAG, message)

fun logError(message: String, throwable: Throwable?) = Log.e(TAG, message, throwable)

fun ViewGroup.inflate(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)

fun <T> Single<T>.io2main(): Single<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.io2main(): Flowable<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.io2main(): Completable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun String.orDefault(default: String) = if (isEmpty()) default else this
