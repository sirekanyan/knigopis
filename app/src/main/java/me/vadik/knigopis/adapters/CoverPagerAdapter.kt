package me.vadik.knigopis.adapters

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import me.vadik.knigopis.doOnSuccess

class CoverPagerAdapter(
    private val urls: List<String>,
    private val onClick: (Int, Boolean) -> Unit,
    private val onFirstLoaded: () -> Unit
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val imageView = ImageView(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setOnClickListener { onClick(position, position == urls.size - 1) }
        }
        Glide.with(context)
            .load(urls[position])
            .doOnSuccess {
                if (position == 0) {
                    onFirstLoaded()
                }
            }
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