package me.vadik.knigopis.api.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import me.vadik.knigopis.model.ImageThumbnail
import me.vadik.knigopis.model.emptyThumbnail
import java.lang.reflect.Type

class ImageThumbnailDeserializer : JsonDeserializer<ImageThumbnail> {
  override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext) =
      json.asJsonObject
          .getAsJsonObject("data")
          .getAsJsonObject("result")
          .getAsJsonArray("items")
          .firstOrNull()
          ?.asJsonObject
          ?.let { ImageThumbnail("https:" + it["thumbnail"].asString) }
          ?: emptyThumbnail
}