package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.NetworkChecker
import com.sirekanyan.knigopis.model.NoteModel
import com.sirekanyan.knigopis.model.toNoteModel
import com.sirekanyan.knigopis.repository.api.Endpoint
import com.sirekanyan.knigopis.repository.cache.CacheKey
import com.sirekanyan.knigopis.repository.cache.CommonCache
import com.sirekanyan.knigopis.repository.cache.genericType
import com.sirekanyan.knigopis.repository.common.CommonRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface NoteRepository {

    fun observeNotes(): Flowable<List<NoteModel>>

}

class NoteRepositoryImpl(
    private val api: Endpoint,
    private val cache: CommonCache,
    networkChecker: NetworkChecker
) : CommonRepository<List<NoteModel>>(networkChecker),
    NoteRepository {

    override fun observeNotes() = observe()

    override fun loadFromNetwork(): Single<List<NoteModel>> =
        api.getLatestBooksWithNotes().map { it.values.map { it.toNoteModel() } }

    override fun findCached(): Maybe<List<NoteModel>> =
        cache.getFromJson(CacheKey.NOTES, genericType<List<NoteModel>>())

    override fun saveToCache(data: List<NoteModel>): Completable =
        cache.saveToJson(CacheKey.NOTES, data)

}