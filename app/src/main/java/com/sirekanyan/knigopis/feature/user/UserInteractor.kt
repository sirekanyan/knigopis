package com.sirekanyan.knigopis.feature.user

import com.sirekanyan.knigopis.common.ResourceProvider
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.createBookHeaderModel
import com.sirekanyan.knigopis.model.toBookModel
import com.sirekanyan.knigopis.repository.KAuth
import com.sirekanyan.knigopis.repository.Endpoint
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
    private val api: Endpoint,
    private val resources: ResourceProvider
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
                        val header = createBookHeaderModel(resources, year, books.size)
                        val items = books.map { it.toBookModel(header.group) }
                        listOf(header, *items.toTypedArray())
                    }
            }
            .io2main()

}