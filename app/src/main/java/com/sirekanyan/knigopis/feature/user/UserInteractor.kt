package com.sirekanyan.knigopis.feature.user

import com.sirekanyan.knigopis.common.android.ResourceProvider
import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.createBookHeaderModel
import com.sirekanyan.knigopis.model.toBookModel
import com.sirekanyan.knigopis.repository.Endpoint
import io.reactivex.Completable
import io.reactivex.Single

interface UserInteractor {

    fun addFriend(userId: String): Completable

    fun removeFriend(userId: String): Completable

    fun isFriend(userId: String): Single<Boolean>

    fun getBooks(userId: String): Single<List<BookModel>>

}

class UserInteractorImpl(
    private val api: Endpoint,
    private val resources: ResourceProvider
) : UserInteractor {

    override fun addFriend(userId: String) =
        api.createSubscription(userId)
            .io2main()

    override fun removeFriend(userId: String) =
        api.deleteSubscription(userId)
            .io2main()

    override fun isFriend(userId: String) =
        api.getSubscriptions()
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