package com.sirekanyan.knigopis.model

import android.net.Uri
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.ResourceProvider

class ProfileItem(val uri: Uri, resource: ResourceProvider) {
    private val social = uri.toSocialNetwork()
    val title = social?.titleRes?.let { resource.getString(it) } ?: "${uri.scheme}://${uri.host}"
    val iconRes = social?.iconRes ?: R.drawable.ic_public
}