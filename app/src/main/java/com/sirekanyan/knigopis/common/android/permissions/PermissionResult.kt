package com.sirekanyan.knigopis.common.android.permissions

import android.content.pm.PackageManager

class PermissionResult(
    private val requestCode: Int,
    val permission: Permission,
    private val result: Int
) {

    fun isGranted(): Boolean =
        result == PackageManager.PERMISSION_GRANTED

    fun isRegularRequest(): Boolean =
        requestCode == permission.requestCode

    companion object {

        fun create(requestCode: Int, permissionKey: String, result: Int): PermissionResult? {
            val permission = Permission.findByKey(permissionKey) ?: return null
            if (requestCode !in permission.requestCodes) return null
            return PermissionResult(requestCode, permission, result)
        }

    }

}