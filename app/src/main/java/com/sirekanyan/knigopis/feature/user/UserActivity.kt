package com.sirekanyan.knigopis.feature.user

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sirekanyan.knigopis.R
import com.sirekanyan.knigopis.common.BaseActivity
import com.sirekanyan.knigopis.common.extensions.systemClipboardManager
import com.sirekanyan.knigopis.common.functions.extra
import com.sirekanyan.knigopis.dependency.providePresenter
import com.sirekanyan.knigopis.feature.book.createBookIntent
import com.sirekanyan.knigopis.model.EditBookModel

private val EXTRA_USER_ID = extra("user_id")
private val EXTRA_USER_NAME = extra("user_name")

fun Context.createUserIntent(id: String, name: String): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, id)
        .putExtra(EXTRA_USER_NAME, name)

class UserActivity : BaseActivity(), UserPresenter.Router {

    private val presenter by lazy {
        providePresenter(
            checkNotNull(intent.getStringExtra(EXTRA_USER_ID)),
            checkNotNull(intent.getStringExtra(EXTRA_USER_NAME))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        systemClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text))
    }

}