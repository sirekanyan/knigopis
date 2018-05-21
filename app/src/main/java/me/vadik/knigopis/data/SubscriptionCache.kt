package me.vadik.knigopis.data

import io.reactivex.Completable
import io.reactivex.Maybe
import me.vadik.knigopis.common.CacheKey
import me.vadik.knigopis.common.CommonCache
import me.vadik.knigopis.common.genericType
import me.vadik.knigopis.model.subscription.Subscription

interface SubscriptionCache {

    fun getSubscriptions(): Maybe<List<Subscription>>

    fun saveSubscriptions(subscriptions: List<Subscription>): Completable

}

class SubscriptionCacheImpl(private val commonCache: CommonCache) : SubscriptionCache {

    override fun getSubscriptions(): Maybe<List<Subscription>> =
        commonCache.getFromJson(CacheKey.SUBSCRIPTIONS, genericType<List<Subscription>>())

    override fun saveSubscriptions(subscriptions: List<Subscription>): Completable =
        commonCache.saveToJson(CacheKey.SUBSCRIPTIONS, subscriptions)

}