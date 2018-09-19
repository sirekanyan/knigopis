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

    fun findCached(): Maybe<List<BookModel>>

    fun saveBook(bookId: String?, book: FinishedBookToSend, wasFinished: Boolean): Completable

    fun saveBook(bookId: String?, book: PlannedBookToSend, wasPlanned: Boolean): Completable

    fun deleteBook(book: BookDataModel): Completable

}

class BookRepositoryImpl(
    private val api: Endpoint,
    private val cache: CommonCache,
    private val plannedBookOrganizer: BookOrganizer<PlannedBook>,
    private val finishedBookOrganizer: BookOrganizer<FinishedBook>,
    networkChecker: NetworkChecker
) : CommonRepository<List<BookModel>>(networkChecker),
    BookRepository {

    override fun observeBooks() = observe()

    override fun saveBook(bookId: String?, book: FinishedBookToSend, wasFinished: Boolean): Completable =
        when {
            bookId == null -> api.createFinishedBook(book)
            wasFinished -> api.updateFinishedBook(bookId, book)
            else -> {
                api.createFinishedBook(book)
                    .andThen(api.deletePlannedBook(bookId))
            }
        }

    override fun saveBook(bookId: String?, book: PlannedBookToSend, wasPlanned: Boolean): Completable =
        when {
            bookId == null -> api.createPlannedBook(book)
            wasPlanned -> api.updatePlannedBook(bookId, book)
            else -> {
                api.createPlannedBook(book)
                    .andThen(api.deleteFinishedBook(bookId))
            }
        }

    override fun deleteBook(book: BookDataModel): Completable =
        if (book.isFinished) {
            api.deleteFinishedBook(book.id)
        } else {
            api.deletePlannedBook(book.id)
        }

    override fun loadFromNetwork(): Single<List<BookModel>> =
        Singles.zip(
            api.getPlannedBooks(),
            api.getFinishedBooks()
        )
            .map { (planned, finished) ->
                plannedBookOrganizer.organize(planned)
                    .plus(finishedBookOrganizer.organize(finished))
            }

    override fun findCached(): Maybe<List<BookModel>> =
        cache.find(CacheKey.BOOKS, genericType<List<BookModel>>())

    override fun saveToCache(data: List<BookModel>): Completable =
        cache.save(CacheKey.BOOKS, data)

}