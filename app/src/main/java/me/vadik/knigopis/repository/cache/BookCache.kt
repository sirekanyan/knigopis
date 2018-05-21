package me.vadik.knigopis.repository.cache

import io.reactivex.Completable
import io.reactivex.Maybe
import me.vadik.knigopis.repository.cache.common.CacheKey
import me.vadik.knigopis.repository.cache.common.CommonCache
import me.vadik.knigopis.repository.cache.common.genericType
import me.vadik.knigopis.repository.model.FinishedBook
import me.vadik.knigopis.repository.model.PlannedBook

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