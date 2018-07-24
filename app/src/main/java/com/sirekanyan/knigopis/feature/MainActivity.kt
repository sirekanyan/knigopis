package com.sirekanyan.knigopis.feature

import android.Manifest.permission.READ_PHONE_STATE
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.Router
import com.sirekanyan.knigopis.common.BaseActivity
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.startActivityOrNull
import com.sirekanyan.knigopis.common.extensions.toast
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.createParameters
import com.sirekanyan.knigopis.feature.book.createEditBookIntent
import com.sirekanyan.knigopis.feature.book.createNewBookIntent
import com.sirekanyan.knigopis.feature.profile.createProfileIntent
import com.sirekanyan.knigopis.feature.user.createUserIntent
import com.sirekanyan.knigopis.feature.users.getMainState
import com.sirekanyan.knigopis.feature.users.saveMainState
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.repository.Configuration
import com.sirekanyan.knigopis.repository.Endpoint
import com.sirekanyan.knigopis.repository.KAuth
import com.tbruyelle.rxpermissions2.RxPermissions
import org.koin.android.ext.android.inject

private const val ULOGIN_REQUEST_CODE = 0
private const val BOOK_REQUEST_CODE = 1

class MainActivity : BaseActivity(), Router, MainPresenter.Router {

    private val presenter by inject<MainPresenter>(parameters = createParameters(this))
    private val api by inject<Endpoint>()
    private val config by inject<Configuration>()
    private val auth by inject<KAuth>()
    private var userLoggedIn = false
    private var booksChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (config.isDarkTheme) R.style.DarkAppTheme else R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.init(savedInstanceState?.getMainState())
        presenter.refreshNavigation()
    }

    override fun onStart() {
        super.onStart()
        presenter.refreshOptionsMenu()
        auth.requestAccessToken().bind({
            presenter.refreshOptionsMenu()
            if (userLoggedIn) {
                userLoggedIn = false
                presenter.refresh()
            }
        }, {
            logError("cannot check credentials", it)
        })
        if (booksChanged) {
            booksChanged = false
            presenter.refresh(isForce = true)
        }
        intent.data?.also { userUrl ->
            intent.data = null
            val normalizedUri = Uri.parse(userUrl.toString().replaceFirst("/#/", "/"))
            normalizedUri.getQueryParameter("u")?.let { userId ->
                api.getUser(userId)
                    .io2main()
                    .bind({ user ->
                        openUserScreen(userId, user.name, user.photo)
                    }, {
                        logError("Cannot get user", it)
                    })
            }
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.state?.let { outState.saveMainState(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ULOGIN_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    auth.saveTokenResponse(data)
                    userLoggedIn = true
                }
            }
            BOOK_REQUEST_CODE -> {
                booksChanged = resultCode == RESULT_OK
            }
        }
    }

    override fun openBookScreen(book: BookDataModel) {
        startActivityForResult(createEditBookIntent(book), BOOK_REQUEST_CODE)
    }

    override fun openUserScreen(id: String, name: String, image: String?) {
        startActivity(createUserIntent(id, name, image))
    }

    override fun openWebPage(uri: Uri) {
        startActivityOrNull(Intent(ACTION_VIEW, uri)) ?: toast(R.string.users_info_no_browser)
    }

    override fun onBackPressed() {
        if (!presenter.back()) {
            super.onBackPressed()
        }
    }

    override fun openProfileScreen() {
        startActivity(createProfileIntent())
    }

    override fun reopenScreen() {
        recreate()
    }

    override fun openNewBookScreen() {
        startActivityForResult(createNewBookIntent(), BOOK_REQUEST_CODE)
    }

    override fun login() {
        RxPermissions(this).requestEach(READ_PHONE_STATE).bind({
            when {
                it.granted -> {
                    if (auth.isAuthorized()) {
                        auth.logout()
                        presenter.refresh()
                    } else {
                        startActivityForResult(auth.getTokenRequest(), ULOGIN_REQUEST_CODE)
                    }
                    presenter.refreshOptionsMenu()
                }
                it.shouldShowRequestPermissionRationale -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.permissions_title_no_access)
                        .setMessage(R.string.permissions_message_no_access)
                        .setPositiveButton(R.string.common_button_retry) { _, _ ->
                            login()
                        }
                        .setNegativeButton(R.string.common_button_cancel, null)
                        .setCancelable(false)
                        .show()
                }
                else -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.permissions_title_request)
                        .setMessage(R.string.permissions_message_request)
                        .setPositiveButton(R.string.permissions_button_settings) { _, _ ->
                            startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                )
                            )
                        }
                        .setNegativeButton(R.string.common_button_cancel, null)
                        .setCancelable(false)
                        .show()
                }
            }
        }, {
            logError("cannot request permission", it)
        })
    }

}
