package com.sirekanyan.knigopis.repository.cache

import com.sirekanyan.knigopis.repository.cache.common.CacheKey
import com.sirekanyan.knigopis.repository.cache.common.CommonCache
import com.sirekanyan.knigopis.repository.cache.common.genericType
import com.sirekanyan.knigopis.repository.model.FinishedBook
import com.sirekanyan.knigopis.repository.model.PlannedBook
import io.reactivex.Completable
import io.reactivex.Maybe

interface BookCache {

    fun getPlannedBooks(): Maybe<List<PlannedBook>>

    fun getFinishedBooks(): Maybe<List<FinishedBook>>

    fun savePlannedBooks(planned: List<PlannedBook>): Completable

    fun saveFinishedBooks(finished: List<FinishedBook>): Completable

}

class BookCacheImpl(private val commonCache: CommonCache) : BookCache {

    override fun getPlannedBooks(): Maybe<List<PlannedBook>> =
        commonCache.getFromJson(
            CacheKey.PLANNED_BOOKS,
            genericType<List<PlannedBook>>()
        )

    override fun getFinishedBooks(): Maybe<List<FinishedBook>> =
        commonCache.getFromJson(
            CacheKey.FINISHED_BOOKS,
            genericType<List<FinishedBook>>()
        )

    override fun savePlannedBooks(planned: List<PlannedBook>): Completable =
        commonCache.saveToJson(CacheKey.PLANNED_BOOKS, planned)

    override fun saveFinishedBooks(finished: List<FinishedBook>): Completable =
        commonCache.saveToJson(CacheKey.FINISHED_BOOKS, finished)

}