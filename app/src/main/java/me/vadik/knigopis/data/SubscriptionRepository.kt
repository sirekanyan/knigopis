package me.vadik.knigopis.data

import io.reactivex.Completable
import io.reactivex.Single
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.common.NetworkChecker
import me.vadik.knigopis.logError
import me.vadik.knigopis.logWarn
import me.vadik.knigopis.model.subscription.Subscription

interface SubscriptionRepository {

    fun getSubscriptions(): Single<List<Subscription>>

}

class SubscriptionRepositoryImpl(
    private val api: Endpoint,
    private val cache: SubscriptionCache,
    private val auth: KAuth,
    private val networkChecker: NetworkChecker
) : SubscriptionRepository {

    override fun getSubscriptions(): Single<List<Subscription>> =
        if (networkChecker.isNetworkAvailable()) {
            getFromNetwork()
                .doOnSuccess { saveToCache(it).blockingAwait() }
                .doOnError {
                    logError("Cannot load subscriptions from network", it)
                    logWarn("Getting cached subscriptions")
                }
                .onErrorResumeNext(findInCache())
        } else {
            findInCache()
        }

    private fun getFromNetwork(): Single<List<Subscription>> =
        api.getSubscriptions(auth.getAccessToken())

    private fun findInCache(): Single<List<Subscription>> =
        cache.getSubscriptions().toSingle()

    private fun saveToCache(subscriptions: List<Subscription>): Completable =
        cache.saveSubscriptions(subscriptions)

}