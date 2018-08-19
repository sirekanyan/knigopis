package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.android.NetworkChecker
import com.sirekanyan.knigopis.model.NoteModel
import com.sirekanyan.knigopis.model.dto.Note
import com.sirekanyan.knigopis.model.toNoteModel
import com.sirekanyan.knigopis.repository.cache.CacheKey
import com.sirekanyan.knigopis.repository.cache.CommonCache
import com.sirekanyan.knigopis.repository.cache.genericType
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
        api.getLatestBooksWithNotes().map { it.values.map(Note::toNoteModel) }

    override fun findCached(): Maybe<List<NoteModel>> =
        cache.find(CacheKey.NOTES, genericType<List<NoteModel>>())

    override fun saveToCache(data: List<NoteModel>): Completable =
        cache.save(CacheKey.NOTES, data)

}