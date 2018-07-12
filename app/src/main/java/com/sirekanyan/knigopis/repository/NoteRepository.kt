package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.NetworkChecker
import com.sirekanyan.knigopis.common.logError
import com.sirekanyan.knigopis.common.logWarn
import com.sirekanyan.knigopis.repository.api.Endpoint
import com.sirekanyan.knigopis.repository.cache.NoteCache
import com.sirekanyan.knigopis.repository.model.note.Note
import io.reactivex.Completable
import io.reactivex.Single

interface NoteRepository {

    fun getNotes(): Single<List<Note>>

}

class NoteRepositoryImpl(
    private val api: Endpoint,
    private val cache: NoteCache,
    private val networkChecker: NetworkChecker
) : NoteRepository {

    override fun getNotes(): Single<List<Note>> =
        if (networkChecker.isNetworkAvailable()) {
            getFromNetwork()
                .doOnSuccess { saveToCache(it).blockingAwait() }
                .doOnError {
                    logError("Cannot load notes from network", it)
                    logWarn("Getting cached notes")
                }
                .onErrorResumeNext(findInCache())
        } else {
            findInCache()
        }

    private fun getFromNetwork(): Single<List<Note>> =
        api.getLatestBooksWithNotes().map { it.values.toList() }

    private fun findInCache(): Single<List<Note>> =
        cache.getNotes().toSingle()

    private fun saveToCache(notes: List<Note>): Completable =
        cache.saveNotes(notes)

}