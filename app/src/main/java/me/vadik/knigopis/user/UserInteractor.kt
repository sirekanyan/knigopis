package me.vadik.knigopis.user

import io.reactivex.Completable
import io.reactivex.Single
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.io2main
import me.vadik.knigopis.model.Book
import me.vadik.knigopis.model.BookHeader

interface UserInteractor {

    fun subscribe(userId: String): Completable

    fun unsubscribe(userId: String): Completable

    fun isSubscribed(userId: String): Single<Boolean>

    fun getBooks(userId: String): Single<List<Pair<Book, BookHeader>>>

}

class UserInteractorImpl(
    private val auth: KAuth,
    private val api: Endpoint
) : UserInteractor {

    override fun subscribe(userId: String) =
        api.createSubscription(userId, auth.getAccessToken())
            .toCompletable()
            .io2main()

    override fun unsubscribe(userId: String) =
        api.deleteSubscription(userId, auth.getAccessToken())
            .toCompletable()
            .io2main()

    override fun isSubscribed(userId: String) =
        api.getSubscriptions(auth.getAccessToken())
            .map { subscriptions -> subscriptions.any { it.subUser.id == userId } }
            .io2main()

    override fun getBooks(userId: String): Single<List<Pair<Book, BookHeader>>> =
        api.getUserBooks(userId)
            .map { books ->
                books.groupBy { it.readYear }
                    .toSortedMap(Comparator { year1, year2 ->
                        year2.compareTo(year1)
                    })
                    .flatMap { (year, books) ->
                        val header = BookHeader(year, books.size)
                        val items = books.map { it to header }
                        listOf(header to header, *items.toTypedArray())
                    }
            }
            .io2main()

}