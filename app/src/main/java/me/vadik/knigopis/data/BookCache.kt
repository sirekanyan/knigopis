package me.vadik.knigopis.data

import io.reactivex.Completable
import io.reactivex.Maybe
import me.vadik.knigopis.common.CommonCache
import me.vadik.knigopis.common.genericType
import me.vadik.knigopis.model.FinishedBook
import me.vadik.knigopis.model.PlannedBook

private const val PLANNED_KEY = "planned"
private const val FINISHED_KEY = "finished"

interface BookCache {

    fun getPlannedBooks(): Maybe<List<PlannedBook>>

    fun getFinishedBooks(): Maybe<List<FinishedBook>>

    fun savePlannedBooks(planned: List<PlannedBook>): Completable

    fun saveFinishedBooks(finished: List<FinishedBook>): Completable

}

class BookCacheImpl(private val commonCache: CommonCache) : BookCache {

    override fun getPlannedBooks(): Maybe<List<PlannedBook>> =
        commonCache.getFromJson(PLANNED_KEY, genericType<List<PlannedBook>>())

    override fun getFinishedBooks(): Maybe<List<FinishedBook>> =
        commonCache.getFromJson(FINISHED_KEY, genericType<List<FinishedBook>>())

    override fun savePlannedBooks(planned: List<PlannedBook>): Completable =
        commonCache.saveToJson(PLANNED_KEY, planned)

    override fun saveFinishedBooks(finished: List<FinishedBook>): Completable =
        commonCache.saveToJson(FINISHED_KEY, finished)

}