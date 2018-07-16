package com.sirekanyan.knigopis.common.adapter

interface CommonModel {

    val isHeader: Boolean
    val header: Header
    val data: Data

    interface Header {
        val id: String
    }

    interface Data {
        val id: String
    }

}