package com.sirekanyan.knigopis.common.android.permissions

import android.Manifest

enum class Permission(val key: String, val requestCode: Int, val rationaleRequestCode: Int) {

    PHONE(Manifest.permission.READ_PHONE_STATE, 300, 400);

    val requestCodes = setOf(requestCode, rationaleRequestCode)

    companion object {

        fun findByKey(key: String): Permission? =
            values().find { it.key == key }

    }

}