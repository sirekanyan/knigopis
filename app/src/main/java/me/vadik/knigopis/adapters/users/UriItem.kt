package me.vadik.knigopis.adapters.users

import android.net.Uri
import me.vadik.knigopis.R
import me.vadik.knigopis.common.ResourceProvider

class UriItem(val uri: Uri, resource: ResourceProvider) {
    private val social = uri.toSocialNetwork()
    val title = social?.titleRes?.let { resource.getString(it) } ?: "${uri.scheme}://${uri.host}"
    val iconRes = social?.iconRes ?: R.drawable.ic_public
}