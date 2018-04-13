package me.vadik.knigopis.common

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.widget.ImageView

interface DrawableResource : CommonResource<ImageView> {
    override fun setValueTo(view: ImageView)
}

class PlainDrawableResource(private val drawable: Drawable) : DrawableResource {
    override fun setValueTo(view: ImageView) {
        view.setImageDrawable(drawable)
    }
}

class DrawableIdResource(@DrawableRes private val resId: Int) : DrawableResource {
    override fun setValueTo(view: ImageView) {
        view.setImageResource(resId)
    }
}