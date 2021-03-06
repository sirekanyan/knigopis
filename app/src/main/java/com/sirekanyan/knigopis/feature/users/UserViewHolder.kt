package com.sirekanyan.knigopis.feature.users

import android.view.View
import com.sirekanyan.knigopis.common.android.adapter.CommonViewHolder
import com.sirekanyan.knigopis.common.extensions.setCircleImage
import com.sirekanyan.knigopis.model.UserModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user.*

class UserViewHolder(
    override val containerView: View,
    private val onClick: (UserModel) -> Unit,
    private val onLongClick: (UserModel) -> Unit
) : CommonViewHolder<UserModel>(containerView),
    LayoutContainer {

    init {
        containerView.setOnClickListener {
            model?.let { user ->
                onClick(user)
            }
        }
        containerView.setOnLongClickListener {
            model?.let { user ->
                onLongClick(user)
            }
            true
        }
    }

    override fun onBind(position: Int, model: UserModel) {
        userImage.setCircleImage(model.image)
        userNickname.text = model.name
        totalBooksCount.text = model.booksCount?.toString()
        newBooksCount.text = model.newBooksCountFormatted
    }

}