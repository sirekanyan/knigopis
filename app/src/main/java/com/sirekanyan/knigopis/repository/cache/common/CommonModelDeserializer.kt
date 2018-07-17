package com.sirekanyan.knigopis.repository.cache.common

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sirekanyan.knigopis.common.adapter.CommonModel
import java.lang.reflect.Type

class CommonModelDeserializer<T : CommonModel>(
    private val headerType: Type,
    private val dataType: Type
) : JsonDeserializer<T> {

    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): T {
        json as JsonObject
        val isHeader = json.get("isHeader").asBoolean
        val actualType = if (isHeader) headerType else dataType
        return context.deserialize(json, actualType)
    }

}
