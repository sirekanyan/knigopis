package com.sirekanyan.knigopis.feature.users

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.extensions.inflate
import com.sirekanyan.knigopis.model.UserModel

class UsersAdapter(
    private val onClick: (UserModel) -> Unit,
    private val onLongClick: (UserModel) -> Unit
) : ListAdapter<UserModel, UserViewHolder>(UserItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserViewHolder(parent.inflate(R.layout.user), onClick, onLongClick)

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(position, getItem(position))
    }

}