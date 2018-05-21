package me.vadik.knigopis.data

import io.reactivex.Completable
import io.reactivex.Maybe
import me.vadik.knigopis.common.CacheKey
import me.vadik.knigopis.common.CommonCache
import me.vadik.knigopis.common.genericType
import me.vadik.knigopis.model.note.Note

interface NoteCache {

    fun getNotes(): Maybe<List<Note>>

    fun saveNotes(notes: List<Note>): Completable

}

class NoteCacheImpl(private val commonCache: CommonCache) : NoteCache {

    override fun getNotes(): Maybe<List<Note>> =
        commonCache.getFromJson(CacheKey.NOTES, genericType<List<Note>>())

    override fun saveNotes(notes: List<Note>): Completable =
        commonCache.saveToJson(CacheKey.NOTES, notes)

}