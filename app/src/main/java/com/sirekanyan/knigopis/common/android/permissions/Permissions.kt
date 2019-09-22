package com.sirekanyan.knigopis.common.android.permissions

import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.android.permissions.Permissions.Callback
import com.sirekanyan.knigopis.common.functions.createAppSettingsIntent
import com.sirekanyan.knigopis.repository.Configuration

interface Permissions {

    fun requestPermission(permission: Permission)
    fun submitResult(result: PermissionResult)

    interface Callback {

        fun onGranted(permission: Permission)

    }

}

class PermissionsImpl(
    private val activity: AppCompatActivity,
    private val config: Configuration
) : Permissions {

    lateinit var callback: Callback

    override fun requestPermission(permission: Permission) {
        when (ContextCompat.checkSelfPermission(activity, permission.key)) {
            PERMISSION_GRANTED -> callback.onGranted(permission)
            PERMISSION_DENIED -> onPermissionDenied(permission)
        }
    }

    private fun onPermissionDenied(permission: Permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.key)) {
            showRationaleDialog {
                requestPermission(permission, permission.rationaleRequestCode)
            }
        } else {
            requestPermission(permission, permission.requestCode)
        }
    }

    private fun requestPermission(permission: Permission, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission.key), requestCode)
    }

    override fun submitResult(result: PermissionResult) {
        when {
            result.isGranted() -> {
                callback.onGranted(result.permission)
            }
            result.isRegularRequest() -> {
                if (config.shouldShowSettings) {
                    showSettingsDialog()
                }
                config.shouldShowSettings = true
            }
        }
    }

    private inline fun showRationaleDialog(crossinline onAllowed: () -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.permissions_title)
            .setMessage(R.string.permissions_message_rationale)
            .setPositiveButton(R.string.permissions_button_rationale) { _, _ -> onAllowed() }
            .setNegativeButton(R.string.common_button_cancel, null)
            .setCancelable(false)
            .show()
    }

    private fun showSettingsDialog() {
        val contentView = activity.findViewById<View>(android.R.id.content)
        Snackbar.make(contentView, R.string.permissions_message_settings, Snackbar.LENGTH_LONG)
            .setAction(R.string.permissions_button_settings) { openSettings() }
            .show()
    }

    private fun openSettings() {
        activity.startActivity(activity.createAppSettingsIntent())
    }

}