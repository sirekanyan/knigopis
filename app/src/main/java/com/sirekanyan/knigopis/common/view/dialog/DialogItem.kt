package com.sirekanyan.knigopis.common.view.dialog

import android.support.annotation.DrawableRes
import com.sirekanyan.knigopis.common.IntegerStringResource
import com.sirekanyan.knigopis.common.PlainStringResource
import com.sirekanyan.knigopis.common.StringResource

fun createDialogItem(titleRes: Int, iconRes: Int, onClick: () -> Unit) =
    DialogItem(IntegerStringResource(titleRes), iconRes, onClick)

fun createDialogItem(title: String, iconRes: Int, onClick: () -> Unit) =
    DialogItem(PlainStringResource(title), iconRes, onClick)

class DialogItem(
    val title: StringResource,
    @DrawableRes val iconRes: Int,
    val onClick: () -> Unit
)