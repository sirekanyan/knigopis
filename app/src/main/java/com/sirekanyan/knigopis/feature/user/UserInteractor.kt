package com.sirekanyan.knigopis.feature.user

import com.sirekanyan.knigopis.common.io2main
import com.sirekanyan.knigopis.model.BookHeaderModel
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.toBookModel
import com.sirekanyan.knigopis.repository.KAuth
import com.sirekanyan.knigopis.repository.api.Endpoint
import io.reactivex.Completable
import io.reactivex.Single

interface UserInteractor {

    fun subscribe(userId: String): Completable

    fun unsubscribe(userId: String): Completable

    fun isSubscribed(userId: String): Single<Boolean>

    fun getBooks(userId: String): Single<List<BookModel>>

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

    override fun getBooks(userId: String): Single<List<BookModel>> =
        api.getUserBooks(userId)
            .map { books ->
                books.groupBy { it.readYear }
                    .toSortedMap(Comparator { year1, year2 ->
                        year2.compareTo(year1)
                    })
                    .flatMap { (year, books) ->
                        val header = BookHeaderModel(year, books.size)
                        val items = books.map { it.toBookModel(header.group) }
                        listOf(header, *items.toTypedArray())
                    }
            }
            .io2main()

}