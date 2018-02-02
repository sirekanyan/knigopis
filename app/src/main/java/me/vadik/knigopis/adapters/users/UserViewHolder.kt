package me.vadik.knigopis.adapters.users

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.user.view.*
import me.vadik.knigopis.R

class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    var avatarUrl: String? = null
        set(value) {
            field = value
            Glide.with(view.context)
                .load(value)
                .apply(
                    RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.oval_placeholder_background)
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view.userAvatar)
        }

    var nickname: String
        get() = view.userNickname.text.toString()
        set(value) {
            view.userNickname.text = value
        }

    var profile: String
        get() = view.userProfile.text.toString()
        set(value) {
            view.userProfile.text = value
        }
}