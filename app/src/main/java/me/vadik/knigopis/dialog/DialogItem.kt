package me.vadik.knigopis.dialog

import android.support.annotation.DrawableRes
import me.vadik.knigopis.common.IntegerStringResource
import me.vadik.knigopis.common.PlainStringResource
import me.vadik.knigopis.common.StringResource

fun createDialogItem(titleRes: Int, iconRes: Int, onClick: () -> Unit) =
    DialogItem(IntegerStringResource(titleRes), iconRes, onClick)

fun createDialogItem(title: String, iconRes: Int, onClick: () -> Unit) =
    DialogItem(PlainStringResource(title), iconRes, onClick)

class DialogItem(
    val title: StringResource,
    @DrawableRes val iconRes: Int,
    val onClick: () -> Unit
)