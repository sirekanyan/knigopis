package me.vadik.knigopis.repository.cache

import io.reactivex.Completable
import io.reactivex.Maybe
import me.vadik.knigopis.repository.cache.common.CacheKey
import me.vadik.knigopis.repository.cache.common.CommonCache
import me.vadik.knigopis.repository.cache.common.genericType
import me.vadik.knigopis.repository.model.note.Note

interface NoteCache {

    fun getNotes(): Maybe<List<Note>>

    fun saveNotes(notes: List<Note>): Completable

}

class NoteCacheImpl(private val commonCache: CommonCache) : NoteCache {

    override fun getNotes(): Maybe<List<Note>> =
        commonCache.getFromJson(
            CacheKey.NOTES,
            genericType<List<Note>>()
        )

    override fun saveNotes(notes: List<Note>): Completable =
        commonCache.saveToJson(CacheKey.NOTES, notes)

}