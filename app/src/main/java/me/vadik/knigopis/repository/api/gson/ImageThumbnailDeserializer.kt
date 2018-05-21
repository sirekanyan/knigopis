package me.vadik.knigopis.repository.api.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import me.vadik.knigopis.repository.model.ImageThumbnail
import java.lang.reflect.Type

class ImageThumbnailDeserializer : JsonDeserializer<ImageThumbnail> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ) =
        json.asJsonObject
            .getAsJsonObject("data")
            .getAsJsonObject("result")
            .getAsJsonArray("items")
            .map { it.asJsonObject["thumbnail"].asString }
            .map { "https:" + it }
            .let(::ImageThumbnail)
}