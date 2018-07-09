package me.vadik.knigopis.repository.api

import io.reactivex.Single
import me.vadik.knigopis.common.io2main
import me.vadik.knigopis.repository.model.ImageThumbnail
import java.util.concurrent.TimeUnit

private const val MAX_DELAY_IN_MICROSECONDS = 3000

interface BookCoverSearch {

    fun search(query: String): Single<List<String>>

}

class BookCoverSearchImpl(private val imageEndpoint: ImageEndpoint) : BookCoverSearch {

    override fun search(query: String) =
        searchThumbnail(query)
            .io2main()

    private fun searchThumbnail(query: String) =
        imageEndpoint.searchImage(query)
            .delay((Math.random() * MAX_DELAY_IN_MICROSECONDS).toLong(), TimeUnit.MICROSECONDS)
            .map(ImageThumbnail::urls)

}