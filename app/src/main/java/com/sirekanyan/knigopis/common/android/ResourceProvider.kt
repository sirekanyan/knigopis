package com.sirekanyan.knigopis.common.android

import android.app.Application

interface ResourceProvider {

    fun getString(id: Int, vararg args: Any): String

    fun getQuantityString(id: Int, quantity: Int, vararg args: Any): String

}

class ResourceProviderImpl(private val app: Application) : ResourceProvider {

    override fun getString(id: Int, vararg args: Any): String =
        app.getString(id, *args)

    override fun getQuantityString(id: Int, quantity: Int, vararg args: Any): String =
        app.resources.getQuantityString(id, quantity, *args)

}