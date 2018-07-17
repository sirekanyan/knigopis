package com.sirekanyan.knigopis.model

import com.sirekanyan.knigopis.common.adapter.CommonModel
import com.sirekanyan.knigopis.repository.api.createBookImageUrl

sealed class BookModel(
    override val id: String,
    override val isHeader: Boolean,
    val group: BookGroupModel
) : CommonModel

class BookHeaderModel(
    val title: String,
    val count: Int
) : BookModel("header-id-$title-$count", true, BookGroupModel(title, count))

class BookDataModel(
    id: String,
    group: BookGroupModel,
    val title: String,
    val author: String,
    val isFinished: Boolean,
    val priority: Int,
    val date: DateModel?,
    val notes: String
) : BookModel(id, false, group) {
    val image = createBookImageUrl(title)
}