package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.NetworkChecker
import com.sirekanyan.knigopis.common.logError
import com.sirekanyan.knigopis.common.logWarn
import com.sirekanyan.knigopis.repository.api.Endpoint
import com.sirekanyan.knigopis.repository.cache.SubscriptionCache
import com.sirekanyan.knigopis.repository.model.subscription.Subscription
import io.reactivex.Completable
import io.reactivex.Single

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