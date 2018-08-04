package com.sirekanyan.knigopis.feature

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BaseActivity
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.common.extensions.startActivityOrNull
import com.sirekanyan.knigopis.common.extensions.toast
import com.sirekanyan.knigopis.common.functions.createAppSettingsIntent
import com.sirekanyan.knigopis.common.functions.createLoginIntent
import com.sirekanyan.knigopis.common.functions.extra
import com.sirekanyan.knigopis.common.functions.logError
import com.sirekanyan.knigopis.createParameters
import com.sirekanyan.knigopis.feature.book.createEditBookIntent
import com.sirekanyan.knigopis.feature.book.createNewBookIntent
import com.sirekanyan.knigopis.feature.books.BooksPresenter
import com.sirekanyan.knigopis.feature.login.LoginPresenter
import com.sirekanyan.knigopis.feature.notes.NotesPresenter
import com.sirekanyan.knigopis.feature.profile.createProfileIntent
import com.sirekanyan.knigopis.feature.user.createUserIntent
import com.sirekanyan.knigopis.feature.users.UsersPresenter
import com.sirekanyan.knigopis.feature.users.getMainState
import com.sirekanyan.knigopis.feature.users.saveMainState
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.CurrentTab
import com.sirekanyan.knigopis.repository.Configuration
import com.sirekanyan.knigopis.repository.Endpoint
import org.koin.android.ext.android.inject
import ru.ulogin.sdk.UloginAuthActivity

private const val LOGIN_REQUEST_CODE = 0
private const val BOOK_REQUEST_CODE = 1
private val CURRENT_TAB_EXTRA = extra("current_tab")

class MainActivity : BaseActivity(),
    MainPresenter.Router,
    LoginPresenter.Router,
    BooksPresenter.Router,
    UsersPresenter.Router,
    NotesPresenter.Router {

    private val presenter by inject<MainPresenter>(parameters = createParameters(this))
    private val api by inject<Endpoint>()
    private val config by inject<Configuration>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (config.isDarkTheme) R.style.DarkAppTheme else R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val restoredCurrentTab = savedInstanceState?.getMainState()?.currentTab
        val currentTab = intent.getIntExtra(CURRENT_TAB_EXTRA, 0)
            .takeUnless { it == 0 }
            ?.let { CurrentTab.getByItemId(it) }
        presenter.init(restoredCurrentTab ?: currentTab)
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
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

    override fun onResume() {
        super.onResume()
        presenter.resume()
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
            LOGIN_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    val userData = data.getSerializableExtra(UloginAuthActivity.USERDATA)
                    val token = (userData as HashMap<*, *>)["token"].toString()
                    presenter.onLoginScreenResult(token)
                }
            }
            BOOK_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    presenter.onBookScreenResult()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!presenter.back()) {
            super.onBackPressed()
        }
    }

    override fun openLoginScreen() {
        startActivityForResult(createLoginIntent(), LOGIN_REQUEST_CODE)
    }

    override fun openSettingsScreen() {
        startActivity(createAppSettingsIntent())
    }

    override fun openProfileScreen() {
        startActivity(createProfileIntent())
    }

    override fun openNewBookScreen() {
        startActivityForResult(createNewBookIntent(), BOOK_REQUEST_CODE)
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

    override fun reopenScreen() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent.also { intent ->
            presenter.state?.let { state ->
                intent.putExtra(CURRENT_TAB_EXTRA, state.currentTab.itemId)
            }
        })
    }

}
