package me.vadik.knigopis.common.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import me.vadik.knigopis.R

fun ImageView.setCircleImage(url: String?) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions.circleCropTransform()
                .placeholder(R.drawable.oval_placeholder_background)
                .theme(context.theme)
        )
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}