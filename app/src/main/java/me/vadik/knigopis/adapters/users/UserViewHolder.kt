package me.vadik.knigopis.adapters.users

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.user.view.*
import me.vadik.knigopis.R
import me.vadik.knigopis.getHtmlString
import me.vadik.knigopis.showNow

class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private val context = view.context.applicationContext

    fun setAvatarUrl(url: String?) =
        Glide.with(view.context)
            .load(url)
            .apply(
                RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.oval_placeholder_background)
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view.userAvatar)

    fun setNickname(nickname: String) {
        view.userNickname.text = nickname
    }

    fun setBooksCount(count: Int) {
        view.totalBooksCount.showNow(count > 0)
        view.totalBooksCount.text = count.toString()
    }

    fun setNewBooksCount(count: Int) {
        view.newBooksCount.showNow(count > 0)
        view.newBooksCount.text = context.getHtmlString(R.string.user_new_books_count, count)
    }

}