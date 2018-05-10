package me.vadik.knigopis.common

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import me.vadik.knigopis.R

fun ImageView.setCircleImage(url: String?, placeholder: Int? = null) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions.circleCropTransform()
                .placeholder(placeholder ?: R.drawable.oval_placeholder_background)
        )
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}