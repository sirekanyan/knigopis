package com.sirekanyan.knigopis.common.extensions

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.AttrRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sirekanyan.knigopis.R

private const val DARK_SATURATION = 0.33f

private fun ImageView.setImage(
    url: String?,
    requestOptions: RequestOptions,
    @AttrRes placeholderAttr: Int
) {
    if (isDarkTheme) {
        val colorMatrix = ColorMatrix().apply { setSaturation(DARK_SATURATION) }
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }
    val placeholder = TypedValue().let { typedValue ->
        context.theme.resolveAttribute(placeholderAttr, typedValue, false)
        typedValue.data
    }
    Glide.with(context)
        .load(url)
        .apply(requestOptions.placeholder(placeholder))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.setCircleImage(url: String?, @AttrRes placeholder: Int? = null) {
    setImage(
        url,
        RequestOptions.circleCropTransform(),
        placeholder ?: R.attr.oval_placeholder_drawable
    )
}

fun ImageView.setSquareImage(url: String?) {
    setImage(
        url,
        RequestOptions.centerCropTransform(),
        R.attr.rectangle_placeholder_drawable
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