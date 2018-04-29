package me.vadik.knigopis.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.profile_activity.*
import kotlinx.android.synthetic.main.profile_contact_item.*
import me.vadik.knigopis.R
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.common.createTextShareIntent
import me.vadik.knigopis.io2main
import me.vadik.knigopis.logError
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.PlannedBook
import org.koin.android.ext.android.inject
import java.util.*

fun Context.createProfileIntent() = Intent(this, ProfileActivity::class.java)

class ProfileActivity : AppCompatActivity() {

    private val api by inject<Endpoint>()
    private val auth by inject<KAuth>()
    private val random = Random()
    private val todoList = mutableListOf<Book>()
    private val doingList = mutableListOf<Book>()
    private val doneList = mutableListOf<Book>()
    private var profileUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)
        initToolbar(profileToolbar)
        profileTodoCount.text = getString(R.string.profile_caption_todo, 0)
        profileDoingCount.text = getString(R.string.profile_caption_doing, 0)
        profileDoneCount.text = getString(R.string.profile_caption_done, 0)
        profileTodoCount.setOnClickListener {
            setRandomFooterBook(todoList)
        }
        profileDoingCount.setOnClickListener {
            setRandomFooterBook(doingList)
        }
        profileDoneCount.setOnClickListener {
            setRandomFooterBook(doneList)
        }
    }

    private fun setRandomFooterBook(books: List<Book>) {
        val book = books.random() ?: return
        randomProfileBook.text = getString(
            R.string.profile_book_random,
            book.titleOrDefault,
            (book as? PlannedBook)?.priority ?: 100
        )
    }

    override fun onStart() {
        super.onStart()
        api.getProfile(auth.getAccessToken())
            .io2main()
            .subscribe({ user ->
                profileUrl = user.fixedProfile
                profileNickname.text = user.nickname ?: "(не указано имя)"
                profileContactTitle.text = user.identity
                Glide.with(this)
                    .load(user.photo)
                    .apply(
                        RequestOptions.circleCropTransform()
                            .placeholder(R.drawable.oval_placeholder_background)
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(profileAvatar)
            }, {
                logError("cannot get profile", it)
            })
        api.getFinishedBooks(auth.getAccessToken())
            .io2main()
            .subscribe({ finishedBooks ->
                doneList.clearAndAddAll(finishedBooks)
                profileDoneCount.text = getString(R.string.profile_caption_done, doneList.size)
            }, {
                logError("cannot check finished books count", it)
            })
        api.getPlannedBooks(auth.getAccessToken())
            .io2main()
            .subscribe({ plannedBooks ->
                doingList.clearAndAddAll(plannedBooks.filter { it.priority > 0 })
                profileDoingCount.text = getString(R.string.profile_caption_doing, doingList.size)
                todoList.clearAndAddAll(plannedBooks.filter { it.priority == 0 })
                profileTodoCount.text = getString(R.string.profile_caption_todo, todoList.size)
            }, {
                logError("cannot check planned books count", it)
            })
    }

    private fun initToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_close)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.inflateMenu(R.menu.profile_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.option_share_profile -> {
                    profileUrl?.let {
                        startActivity(
                            createTextShareIntent(it, getString(R.string.option_share_title))
                        )
                    }
                    true
                }
                R.id.option_logout_profile -> {
                    auth.logout()
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun <T> List<T>.random(): T? {
        if (size == 0) return null
        return get(random.nextInt(size))
    }

    private fun <T> MutableCollection<T>.clearAndAddAll(collection: Collection<T>) {
        clear()
        addAll(collection)
    }

}