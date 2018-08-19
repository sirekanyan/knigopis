package com.sirekanyan.knigopis.feature.user

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BaseActivity
import com.sirekanyan.knigopis.common.extensions.app
import com.sirekanyan.knigopis.common.extensions.setDarkTheme
import com.sirekanyan.knigopis.common.extensions.systemClipboardManager
import com.sirekanyan.knigopis.common.functions.extra
import com.sirekanyan.knigopis.dependency.providePresenter
import com.sirekanyan.knigopis.feature.book.createBookIntent
import com.sirekanyan.knigopis.model.EditBookModel

private val EXTRA_USER_ID = extra("user_id")
private val EXTRA_USER_NAME = extra("user_name")
private val EXTRA_USER_IMAGE = extra("user_image")

fun Context.createUserIntent(id: String, name: String, avatar: String?): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, id)
        .putExtra(EXTRA_USER_NAME, name)
        .putExtra(EXTRA_USER_IMAGE, avatar)

class UserActivity : BaseActivity(), UserPresenter.Router {

    private val presenter by lazy {
        providePresenter(
            intent.getStringExtra(EXTRA_USER_ID),
            intent.getStringExtra(EXTRA_USER_NAME),
            intent.getStringExtra(EXTRA_USER_IMAGE)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setDarkTheme(app.config.isDarkTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)
        presenter.init()
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun openBookScreen(book: EditBookModel) {
        startActivity(createBookIntent(book))
    }

    override fun copyToClipboard(text: String) {
        systemClipboardManager.primaryClip = ClipData.newPlainText(null, text)
    }

}