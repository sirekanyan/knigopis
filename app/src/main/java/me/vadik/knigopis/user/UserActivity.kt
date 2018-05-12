package me.vadik.knigopis.user

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.user_activity.*
import me.vadik.knigopis.*
import me.vadik.knigopis.adapters.books.BooksAdapter
import me.vadik.knigopis.adapters.books.UserBook
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.common.setCircleImage
import me.vadik.knigopis.dialog.DialogFactory
import me.vadik.knigopis.model.note.Identity
import me.vadik.knigopis.model.subscription.Subscription
import org.koin.android.ext.android.inject

private const val EXTRA_USER_ID = "me.vadik.knigopis.extra_user_id"
private const val EXTRA_USER_NAME = "me.vadik.knigopis.extra_user_name"
private const val EXTRA_USER_PHOTO = "me.vadik.knigopis.extra_user_photo"
private const val EXTRA_USER_PROFILES = "me.vadik.knigopis.extra_user_profiles"

fun Context.createUserIntent(user: Subscription): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, user.subUser.id)
        .putExtra(EXTRA_USER_NAME, user.subUser.name)
        .putExtra(EXTRA_USER_PHOTO, user.subUser.avatar)
        .putExtra(EXTRA_USER_PROFILES, user.subUser.profiles.toTypedArray())

fun Context.createUserIntent(user: Identity): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, user.id)
        .putExtra(EXTRA_USER_NAME, user.nickname)
        .putExtra(EXTRA_USER_PROFILES, arrayOf<Uri>())

class UserActivity : AppCompatActivity() {

    private val api by inject<Endpoint>()
    private val auth by inject<KAuth>()
    private val dialogs by inject<DialogFactory> { mapOf("activity" to this) }
    private val userId by lazy { intent.getStringExtra(EXTRA_USER_ID) }
    private val books = mutableListOf<UserBook>()
    private val booksAdapter = BooksAdapter(books, dialogs)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)
        toolbar.title = intent.getStringExtra(EXTRA_USER_NAME)
        toolbarImage.setCircleImage(intent.getStringExtra(EXTRA_USER_PHOTO), R.drawable.oval_dark_placeholder_background)
        toolbarImage.setElevationRes(R.dimen.image_view_elevation)
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
        val layoutManager = LinearLayoutManager(this)
        userBooksRecyclerView.layoutManager = layoutManager
        userBooksRecyclerView.addItemDecoration(
            DividerItemDecoration(this, layoutManager.orientation)
        )
        userBooksRecyclerView.adapter = booksAdapter
    }

    override fun onStart() {
        super.onStart()
        api.getUserBooks(userId).io2main()
            .doOnSubscribe {
                userBooksProgressBar.show()
                userBooksErrorPlaceholder.hide()
                userBooksRecyclerView.hide()
            }
            .doFinally { userBooksProgressBar.hide() }
            .doOnSuccess { userBooksRecyclerView.show() }
            .doOnError { userBooksErrorPlaceholder.show() }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_copy -> {
                val link = "http://www.knigopis.com/#/user/books?u=$userId"
                systemClipboardManager.primaryClip = ClipData.newPlainText(null, link)
                toast(link)
                true
            }
            R.id.option_unsubscribe -> {
                api.deleteSubscription(userId, auth.getAccessToken())
                    .io2main()
                    .subscribe({}, {
                        logError("Cannot unsubscribe", it)
                        toast("Не удалось отписаться")
                    })
                true
            }
            else -> false
        }
    }
}