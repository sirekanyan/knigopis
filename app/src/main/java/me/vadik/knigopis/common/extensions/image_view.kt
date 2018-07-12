package me.vadik.knigopis.common.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import me.vadik.knigopis.R

fun ImageView.setCircleImage(url: String?, isDark: Boolean = false) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions.circleCropTransform()
                .placeholder(
                    if (isDark) {
                        R.drawable.oval_dark_placeholder_background
                    } else {
                        R.drawable.oval_placeholder_background
                    }
                )
                .theme(context.theme)
        )
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.setSquareImage(url: String?) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions.centerCropTransform()
                .placeholder(R.drawable.rectangle_placeholder_background)
                .theme(context.theme)
        )
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
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