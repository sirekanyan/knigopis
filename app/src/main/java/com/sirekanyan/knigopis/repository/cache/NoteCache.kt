package com.sirekanyan.knigopis.repository.cache

import io.reactivex.Completable
import io.reactivex.Maybe
import com.sirekanyan.knigopis.repository.cache.common.CacheKey
import com.sirekanyan.knigopis.repository.cache.common.CommonCache
import com.sirekanyan.knigopis.repository.cache.common.genericType
import com.sirekanyan.knigopis.repository.model.note.Note

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