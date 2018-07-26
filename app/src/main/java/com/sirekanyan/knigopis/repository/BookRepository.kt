package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.android.NetworkChecker
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.BookModel
import com.sirekanyan.knigopis.model.dto.FinishedBook
import com.sirekanyan.knigopis.model.dto.FinishedBookToSend
import com.sirekanyan.knigopis.model.dto.PlannedBook
import com.sirekanyan.knigopis.model.dto.PlannedBookToSend
import com.sirekanyan.knigopis.repository.cache.CacheKey
import com.sirekanyan.knigopis.repository.cache.CommonCache
import com.sirekanyan.knigopis.repository.cache.genericType
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles

interface BookRepository {

    fun observeBooks(): Flowable<List<BookModel>>

    fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable

    fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable

    fun deleteBook(book: BookDataModel): Completable

}

class BookRepositoryImpl(
    private val api: Endpoint,
    private val cache: CommonCache,
    private val auth: KAuth,
    private val plannedBookOrganizer: BookOrganizer<PlannedBook>,
    private val finishedBookPrepare: BookOrganizer<FinishedBook>,
    networkChecker: NetworkChecker
) : CommonRepository<List<BookModel>>(networkChecker),
    BookRepository {

    override fun observeBooks() = observe()

    override fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable =
        when {
            bookId == null -> api.createFinishedBook(auth.getAccessToken(), book)
            done == null -> Completable.error(UnsupportedOperationException())
            done -> api.updateFinishedBook(bookId, auth.getAccessToken(), book)
            else -> {
                api.createFinishedBook(auth.getAccessToken(), book)
                    .andThen(api.deletePlannedBook(bookId, auth.getAccessToken()))
            }
        }

    override fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable =
        when {
            bookId == null -> api.createPlannedBook(auth.getAccessToken(), book)
            done == null -> Completable.error(UnsupportedOperationException())
            !done -> api.updatePlannedBook(bookId, auth.getAccessToken(), book)
            else -> {
                api.createPlannedBook(auth.getAccessToken(), book)
                    .andThen(api.deleteFinishedBook(bookId, auth.getAccessToken()))
            }
        }

    override fun deleteBook(book: BookDataModel): Completable =
        if (book.isFinished) {
            api.deleteFinishedBook(book.id, auth.getAccessToken())
        } else {
            api.deletePlannedBook(book.id, auth.getAccessToken())
        }

    override fun loadFromNetwork(): Single<List<BookModel>> =
        Singles.zip(
            api.getPlannedBooks(auth.getAccessToken()),
            api.getFinishedBooks(auth.getAccessToken())
        )
            .map { (planned, finished) ->
                mutableListOf<BookModel>().apply {
                    addAll(plannedBookOrganizer.organize(planned))
                    addAll(finishedBookPrepare.organize(finished))
                }
            }

    override fun findCached(): Maybe<List<BookModel>> =
        cache.getFromJson(CacheKey.BOOKS, genericType<List<BookModel>>())

    override fun saveToCache(data: List<BookModel>): Completable =
        cache.saveToJson(CacheKey.BOOKS, data)

}