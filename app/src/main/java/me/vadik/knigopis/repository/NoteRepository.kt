package me.vadik.knigopis.repository

import io.reactivex.Completable
import io.reactivex.Single
import me.vadik.knigopis.repository.api.Endpoint
import me.vadik.knigopis.repository.cache.NoteCache
import me.vadik.knigopis.common.NetworkChecker
import me.vadik.knigopis.common.logError
import me.vadik.knigopis.common.logWarn
import me.vadik.knigopis.repository.model.note.Note

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