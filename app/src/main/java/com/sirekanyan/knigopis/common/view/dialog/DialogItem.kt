package com.sirekanyan.knigopis.common.view.dialog

import android.support.annotation.DrawableRes
import com.sirekanyan.knigopis.common.StringResource

class DialogItem(
    val title: StringResource,
    @DrawableRes val iconRes: Int,
    val onClick: () -> Unit
)