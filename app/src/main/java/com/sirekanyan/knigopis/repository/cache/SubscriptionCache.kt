package com.sirekanyan.knigopis.repository.cache

import com.sirekanyan.knigopis.repository.cache.common.CacheKey
import com.sirekanyan.knigopis.repository.cache.common.CommonCache
import com.sirekanyan.knigopis.repository.cache.common.genericType
import com.sirekanyan.knigopis.repository.model.subscription.Subscription
import io.reactivex.Completable
import io.reactivex.Maybe

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