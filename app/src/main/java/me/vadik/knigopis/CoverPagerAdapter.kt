package me.vadik.knigopis

import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.*
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

class CoverPagerAdapter(
    private val urls: List<String>,
    private val onClick: (Int, Boolean) -> Unit,
    private val onLoaded: (Int) -> Unit
) : PagerAdapter() {

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val context = container.context
    val imageView = ImageView(context).apply {
      layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
      setOnClickListener { onClick(position, position == urls.size - 1) }
    }
    Glide.with(context)
        .load(urls[position])
        .listener(object : RequestListener<Drawable> {
          override fun onResourceReady(
              resource: Drawable?,
              model: Any?,
              target: Target<Drawable>?,
              dataSource: DataSource?,
              isFirstResource: Boolean
          ): Boolean {
            onLoaded(position)
            return false
          }

          override fun onLoadFailed(
              e: GlideException?,
              model: Any?,
              target: Target<Drawable>?,
              isFirstResource: Boolean
          ): Boolean {
            return false
          }
        })
        .apply(RequestOptions.centerCropTransform())
        .into(imageView)
    container.addView(imageView)
    return imageView
  }

  override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
    container.removeView(obj as ImageView)
  }

  override fun isViewFromObject(view: View, obj: Any) = obj == view

  override fun getCount() = urls.size
}