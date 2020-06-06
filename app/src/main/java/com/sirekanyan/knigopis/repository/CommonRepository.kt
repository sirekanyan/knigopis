package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.android.NetworkChecker
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

abstract class CommonRepository<T>(private val networkChecker: NetworkChecker) {

    abstract fun loadFromNetwork(): Single<T>

    abstract fun findCached(): Maybe<T>

    abstract fun saveToCache(data: T): Completable

    fun observe(): Flowable<T> =
        if (networkChecker.isNetworkAvailable()) {
            findCached().concatWith(
                loadFromNetwork()
                    .doOnSuccess { saveToCache(it).blockingAwait() }
                    .toMaybe()
            )
        } else {
            findCached().toSingle().toFlowable()
        }

}