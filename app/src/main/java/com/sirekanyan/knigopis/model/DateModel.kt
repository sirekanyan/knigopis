package com.sirekanyan.knigopis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

val EMPTY_DATE = DateModel("", "", "")

@Parcelize
data class DateModel(
    val year: String,
    val month: String,
    val day: String
) : Parcelable {
    fun isEmpty() = this == EMPTY_DATE
}