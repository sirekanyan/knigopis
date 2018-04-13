package me.vadik.knigopis.dialog

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

class DialogItem(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val onClick: () -> Unit
)