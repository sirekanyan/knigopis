package me.vadik.knigopis.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.user_activity.*
import me.vadik.knigopis.R
import me.vadik.knigopis.adapters.books.BooksAdapter
import me.vadik.knigopis.adapters.books.UserBook
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.app
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.auth.KAuthImpl
import me.vadik.knigopis.io2main
import me.vadik.knigopis.logError
import me.vadik.knigopis.model.note.Identity
import me.vadik.knigopis.model.subscription.Subscription

private const val EXTRA_USER_ID = "me.vadik.knigopis.extra_user_id"
private const val EXTRA_USER_NAME = "me.vadik.knigopis.extra_user_name"
private const val EXTRA_USER_PHOTO = "me.vadik.knigopis.extra_user_photo"

fun Context.createUserIntent(user: Subscription): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, user.subUser.id)
        .putExtra(EXTRA_USER_NAME, user.subUser.nickname)
        .putExtra(EXTRA_USER_PHOTO, user.subUser.photo)

fun Context.createUserIntent(user: Identity): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, user.id)
        .putExtra(EXTRA_USER_NAME, user.nickname)

class UserActivity : AppCompatActivity() {

    private val api by lazy { app().baseApi.create(Endpoint::class.java) }
    private val auth by lazy { KAuthImpl(applicationContext, api) as KAuth }
    private val userId by lazy { intent.getStringExtra(EXTRA_USER_ID) }
    private val books = mutableListOf<UserBook>()
    private val booksAdapter = BooksAdapter(books)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)
        toolbar.title = intent.getStringExtra(EXTRA_USER_NAME)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            fab.hide()
            api.createSubscription(userId, auth.getAccessToken())
                .io2main()
                .subscribe({
                    Snackbar.make(view, "Successfully subscribed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }, {
                    fab.show()
                    logError("Cannot update subscription", it)
                })
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        userBooksRecyclerView.layoutManager = LinearLayoutManager(this)
        userBooksRecyclerView.adapter = booksAdapter
    }

    override fun onStart() {
        super.onStart()
        api.getUserBooks(userId)
            .io2main()
            .subscribe({
                books.clear()
                books.addAll(it)
                booksAdapter.notifyDataSetChanged()
            }, {
                logError("Cannot load user books", it)
            })
        api.getSubscriptions(auth.getAccessToken())
            .io2main()
            .subscribe({ subscriptions ->
                if (subscriptions.none { it.subUser.id == userId }) {
                    fab.show()
                }
            }, {
                logError("Cannot update subscription", it)
            })
    }
}
