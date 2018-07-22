package com.sirekanyan.knigopis.common.view.dialog

import com.sirekanyan.knigopis.common.IntegerStringResource
import com.sirekanyan.knigopis.common.PlainStringResource

fun createDialogItem(titleRes: Int, iconRes: Int, onClick: () -> Unit) =
    DialogItem(IntegerStringResource(titleRes), iconRes, onClick)

fun createDialogItem(title: String, iconRes: Int, onClick: () -> Unit) =
    DialogItem(PlainStringResource(title), iconRes, onClick)