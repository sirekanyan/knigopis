package me.vadik.knigopis.data

import io.reactivex.Completable
import io.reactivex.Maybe
import me.vadik.knigopis.common.CacheKey
import me.vadik.knigopis.common.CommonCache
import me.vadik.knigopis.common.genericType
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.PlannedBook

interface BookCache {

    fun getPlannedBooks(): Maybe<List<PlannedBook>>

    fun getFinishedBooks(): Maybe<List<FinishedBook>>

    fun savePlannedBooks(planned: List<PlannedBook>): Completable

    fun saveFinishedBooks(finished: List<FinishedBook>): Completable

}

class BookCacheImpl(private val commonCache: CommonCache) : BookCache {

    override fun getPlannedBooks(): Maybe<List<PlannedBook>> =
        commonCache.getFromJson(CacheKey.PLANNED_BOOKS, genericType<List<PlannedBook>>())

    override fun getFinishedBooks(): Maybe<List<FinishedBook>> =
        commonCache.getFromJson(CacheKey.FINISHED_BOOKS, genericType<List<FinishedBook>>())

    override fun savePlannedBooks(planned: List<PlannedBook>): Completable =
        commonCache.saveToJson(CacheKey.PLANNED_BOOKS, planned)

    override fun saveFinishedBooks(finished: List<FinishedBook>): Completable =
        commonCache.saveToJson(CacheKey.FINISHED_BOOKS, finished)

}