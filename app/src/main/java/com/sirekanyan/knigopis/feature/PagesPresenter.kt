package com.sirekanyan.knigopis.feature

import com.sirekanyan.knigopis.model.CurrentTab

interface PagesPresenter {
    fun onPageUpdated(tab: CurrentTab)
}