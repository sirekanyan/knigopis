package me.vadik.knigopis.user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.user_activity.*
import me.vadik.knigopis.*
import me.vadik.knigopis.adapters.books.BooksAdapter
import me.vadik.knigopis.adapters.books.UserBook
import me.vadik.knigopis.adapters.users.toSocialNetwork
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.api.ImageEndpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.auth.KAuthImpl
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
        .putExtra(EXTRA_USER_NAME, user.subUser.nickname)
        .putExtra(EXTRA_USER_PHOTO, user.subUser.photo)
        .putExtra(EXTRA_USER_PROFILES, user.subUser.profiles.toTypedArray())

fun Context.createUserIntent(user: Identity): Intent =
    Intent(this, UserActivity::class.java)
        .putExtra(EXTRA_USER_ID, user.id)
        .putExtra(EXTRA_USER_NAME, user.nickname)
        .putExtra(EXTRA_USER_PROFILES, arrayOf<Uri>())

class UserActivity : AppCompatActivity() {

    private val api by inject<Endpoint>()
    private val auth by lazy { KAuthImpl(applicationContext, api) as KAuth }
    private val userId by lazy { intent.getStringExtra(EXTRA_USER_ID) }
    private val books = mutableListOf<UserBook>()
    private val booksAdapter = BooksAdapter(books)
    private lateinit var menuItems: Map<Int, UriItem>

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
        menuItems = intent.getParcelableArrayExtra(EXTRA_USER_PROFILES)
            .filterIsInstance(Uri::class.java)
            .map(::UriItem)
            .distinctBy(UriItem::key)
            .mapIndexed { index, item ->
                Menu.FIRST + index to item
            }
            .toMap()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuItems.forEach { (id, item) ->
            if (item.socialNetworkTitleRes == null) {
                menu.add(Menu.NONE, id, 0, item.personalPageTitle)
            } else {
                menu.add(Menu.NONE, id, 0, item.socialNetworkTitleRes)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuItems[item.itemId]?.uri?.let { uri ->
            startActivityOrElse(Intent(Intent.ACTION_VIEW, uri)) {
                toast("Невозможно открыть страницу")
            }
            return true
        }
        return false
    }
}

private class UriItem(val uri: Uri) {
    val socialNetworkTitleRes = uri.toSocialNetwork()?.titleRes
    val personalPageTitle = uri.scheme + "://" + uri.host
    val key = socialNetworkTitleRes ?: personalPageTitle
}
