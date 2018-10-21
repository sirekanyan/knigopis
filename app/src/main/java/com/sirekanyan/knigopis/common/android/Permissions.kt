package com.sirekanyan.knigopis.common.android

import android.Manifest.permission.READ_PHONE_STATE
import android.app.Activity
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single

interface Permissions {

    fun requestReadPhoneState(): Single<Permission>

}

class PermissionsImpl(private val activity: Activity) : Permissions {

    override fun requestReadPhoneState(): Single<Permission> =
        RxPermissions(activity).requestEach(READ_PHONE_STATE).firstOrError()

}