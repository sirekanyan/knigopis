package me.vadik.knigopis

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val TAG = "Knigopis"
private val HTTP_SCHEMES = setOf("http", "https")

inline fun Context.startActivityOrElse(intent: Intent, onError: () -> Unit) {
    if (packageManager.resolveActivity(intent, 0) == null) {
        onError()
    } else {
        startActivity(intent)
    }
}

val Context.systemClipboardManager: ClipboardManager
    get() = getAndroidSystemService(Context.CLIPBOARD_SERVICE)

val Context.systemInputMethodManager: InputMethodManager
    get() = getAndroidSystemService(Context.INPUT_METHOD_SERVICE)

private inline fun <reified T> Context.getAndroidSystemService(name: String) =
    getSystemService(name) as T

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toast(@StringRes messageId: Int) =
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()

fun Activity.app() = application as App

@Suppress("unused")
fun logWarn(message: String) = Log.w(TAG, message)

fun logError(message: String, throwable: Throwable?) = Log.e(TAG, message, throwable)

fun ViewGroup.inflate(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)

fun <T> Single<T>.io2main(): Single<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.io2main(): Completable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun String.orDefault(default: String) = if (isEmpty()) default else this

fun <T> RequestBuilder<T>.doOnSuccess(onSuccess: () -> Unit): RequestBuilder<T> =
    listener(object : RequestListener<T> {
        override fun onResourceReady(
            resource: T?,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onSuccess()
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }
    })

fun View.showNow() {
    visibility = View.VISIBLE
}

fun View.showNow(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}

fun View.hideNow() {
    visibility = View.GONE
}

fun View.show(value: Boolean) {
    if (value) show() else hide()
}

fun View.show() {
    animate().alpha(1f).setDuration(200)
        .withStartAction { visibility = View.VISIBLE }
        .start()
}

fun View.hide() {
    animate().alpha(0f).setDuration(200)
        .withEndAction { visibility = View.GONE }
        .start()
}

val View.isVisible get() = visibility == View.VISIBLE

fun ProgressBar.setProgressSmoothly(progress: Int) {
    ObjectAnimator.ofInt(this, "progress", progress).start()
}

fun Activity.hideKeyboard() {
    currentFocus?.let { view ->
        systemInputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun String.toUriOrNull() =
    Uri.parse(this).takeIf(Uri::isValidHttpLink)

fun Context.getHtmlString(resId: Int, vararg args: Any) =
    getString(resId, *args).fromHtml()

private fun String.fromHtml(): Spanned =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }

private fun Uri.isValidHttpLink() =
    scheme in HTTP_SCHEMES && !host.isNullOrBlank()