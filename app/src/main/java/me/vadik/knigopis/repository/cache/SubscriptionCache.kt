package me.vadik.knigopis.repository.cache

import io.reactivex.Completable
import io.reactivex.Maybe
import me.vadik.knigopis.repository.cache.common.CacheKey
import me.vadik.knigopis.repository.cache.common.CommonCache
import me.vadik.knigopis.repository.cache.common.genericType
import me.vadik.knigopis.repository.model.subscription.Subscription

interface SubscriptionCache {

    fun getSubscriptions(): Maybe<List<Subscription>>

    fun saveSubscriptions(subscriptions: List<Subscription>): Completable

}

class SubscriptionCacheImpl(private val commonCache: CommonCache) :
    SubscriptionCache {

    override fun getSubscriptions(): Maybe<List<Subscription>> =
        commonCache.getFromJson(
            CacheKey.SUBSCRIPTIONS,
            genericType<List<Subscription>>()
        )

    override fun saveSubscriptions(subscriptions: List<Subscription>): Completable =
        commonCache.saveToJson(CacheKey.SUBSCRIPTIONS, subscriptions)

}