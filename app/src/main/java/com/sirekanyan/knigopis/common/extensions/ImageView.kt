package com.sirekanyan.knigopis.common.extensions

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.TransitionFactory
import com.sirekanyan.knigopis.R

private const val DARK_SATURATION = 0.33f
private val crossFadeTransitionFactory = TransitionFactory { _, _ ->
    DrawableCrossFadeTransition(300, true)
}

private fun ImageView.setImage(
    url: String?,
    requestOptions: RequestOptions,
    @DrawableRes placeholderRes: Int
) {
    if (context.isNightMode) {
        val colorMatrix = ColorMatrix().apply { setSaturation(DARK_SATURATION) }
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }
    val placeholder = ContextCompat.getDrawable(context, placeholderRes)
    Glide.with(context)
        .load(url)
        .apply(requestOptions.placeholder(placeholder))
        .transition(DrawableTransitionOptions.with(crossFadeTransitionFactory))
        .into(this)
}

fun ImageView.setCircleImage(url: String?) {
    setImage(
        url,
        RequestOptions.circleCropTransform(),
        R.drawable.oval_placeholder_background
    )
}

fun ImageView.setCircleImageOnPrimary(url: String?) {
    setImage(
        url,
        RequestOptions.circleCropTransform(),
        R.drawable.oval_placeholder_on_primary
    )
}

fun ImageView.setSquareImage(url: String?) {
    setImage(
        url,
        RequestOptions.centerCropTransform(),
        R.drawable.rectangle_placeholder_background
    )
}

fun Context.preloadImage(url: String?, onSuccess: () -> Unit, onError: () -> Unit) {
    Glide.with(this)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onSuccess()
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                onError()
                return false
            }
        })
        .preload()
}