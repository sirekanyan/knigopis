package me.vadik.knigopis.api.gson

import com.google.gson.GsonBuilder
import me.vadik.knigopis.model.Notes
import org.junit.Assert.assertEquals
import org.junit.Test

class NotesTypeAdapterTest {

    private val gson = GsonBuilder()
        .registerTypeAdapter(Notes::class.java, NotesTypeAdapter())
        .create()

    @Test
    fun write() {
        val notes = Notes("text", 25)
        assertEquals("\"text // 25%\"", gson.toJson(notes))
    }

    @Test
    fun writeZeroProgress() {
        val notes = Notes("text", 0)
        assertEquals("\"text\"", gson.toJson(notes))
    }

    @Test
    fun writeEmptyText() {
        val notes = Notes("", 30)
        assertEquals("\"// 30%\"", gson.toJson(notes))
    }

    @Test
    fun read() {
        val notes = gson.fromJson("\"text // text // 25%\"", Notes::class.java)
        assertEquals("text // text", notes.text)
        assertEquals(25, notes.progress)
    }

    @Test
    fun readZeroProgress() {
        val notes = gson.fromJson("\"text // text\"", Notes::class.java)
        assertEquals("text // text", notes.text)
        assertEquals(0, notes.progress)
    }

    @Test
    fun readEmptyText() {
        val notes = gson.fromJson("\"// 30%\"", Notes::class.java)
        assertEquals("", notes.text)
        assertEquals(30, notes.progress)
    }
}