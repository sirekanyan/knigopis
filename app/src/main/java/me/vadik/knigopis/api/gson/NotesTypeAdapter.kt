package me.vadik.knigopis.api.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import me.vadik.knigopis.model.Notes

class NotesTypeAdapter : TypeAdapter<Notes>() {

    private val regex = Regex("(.*)// (\\d+)%")

    override fun write(output: JsonWriter, notes: Notes) {
        if (notes.progress == 0) {
            output.value(notes.text)
        } else {
            output.value("${notes.text} // ${notes.progress}%".trim())
        }
    }

    override fun read(input: JsonReader): Notes {
        val fullText = input.nextString()
        return regex.matchEntire(fullText)?.let {
            Notes(it.groupValues[1].trim(), it.groupValues[2].toInt())
        } ?: Notes(fullText, 0)
    }
}