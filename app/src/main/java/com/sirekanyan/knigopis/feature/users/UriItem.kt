package com.sirekanyan.knigopis.feature.users

import android.net.Uri
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.ResourceProvider

class UriItem(val uri: Uri, resource: ResourceProvider) {
    private val social = uri.toSocialNetwork()
    val title = social?.titleRes?.let { resource.getString(it) } ?: "${uri.scheme}://${uri.host}"
    val iconRes = social?.iconRes ?: R.drawable.ic_public
}