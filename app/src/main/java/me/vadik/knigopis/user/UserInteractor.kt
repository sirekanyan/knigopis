package me.vadik.knigopis.user

import io.reactivex.Completable
import io.reactivex.Single
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.io2main
import me.vadik.knigopis.model.FinishedBook

interface UserInteractor {

    fun subscribe(userId: String): Completable

    fun unsubscribe(userId: String): Completable

    fun isSubscribed(userId: String): Single<Boolean>

    fun getBooks(userId: String): Single<List<FinishedBook>>

}

class UserInteractorImpl(
    private val auth: KAuth,
    private val api: Endpoint,
    private val userRepository: UserRepository
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

    override fun getBooks(userId: String) =
        api.getUserBooks(userId)
            .io2main()

}