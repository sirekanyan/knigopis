package com.sirekanyan.knigopis.common

import android.app.Application

interface ResourceProvider {
    fun getString(id: Int, vararg args: Any): String
}

class ResourceProviderImpl(private val app: Application) : ResourceProvider {
    override fun getString(id: Int, vararg args: Any): String = app.getString(id, *args)
}