package com.sirekanyan.knigopis.repository

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import com.sirekanyan.knigopis.common.NetworkChecker
import com.sirekanyan.knigopis.common.logError
import com.sirekanyan.knigopis.common.logWarn
import com.sirekanyan.knigopis.repository.api.Endpoint
import com.sirekanyan.knigopis.repository.cache.BookCache
import com.sirekanyan.knigopis.repository.model.*

interface BookRepository {

    fun loadBooks(): Single<List<Pair<Book, BookHeader>>>

    fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable

    fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable

}

class BookRepositoryImpl(
    private val api: Endpoint,
    private val cache: BookCache,
    private val auth: KAuth,
    private val plannedBookOrganizer: BookOrganizer<PlannedBook>,
    private val finishedBookPrepare: BookOrganizer<FinishedBook>,
    private val networkChecker: NetworkChecker
) : BookRepository {

    override fun loadBooks(): Single<List<Pair<Book, BookHeader>>> =
        if (networkChecker.isNetworkAvailable()) {
            getFromNetwork()
                .doOnSuccess { saveToCache(it).blockingAwait() }
                .doOnError {
                    logError("Cannot load books from network", it)
                    logWarn("Getting cached books")
                }
                .onErrorResumeNext(findInCache())
        } else {
            findInCache()
        }.map { (planned, finished) ->
            mutableListOf<Pair<Book, BookHeader>>().apply {
                addAll(plannedBookOrganizer.organize(planned))
                addAll(finishedBookPrepare.organize(finished))
            }
        }

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

    private fun getFromNetwork(): Single<Pair<List<PlannedBook>, List<FinishedBook>>> =
        Singles.zip(
            api.getPlannedBooks(auth.getAccessToken()),
            api.getFinishedBooks(auth.getAccessToken())
        )

    private fun findInCache(): Single<Pair<List<PlannedBook>, List<FinishedBook>>> =
        Singles.zip(
            cache.getPlannedBooks().toSingle(),
            cache.getFinishedBooks().toSingle()
        )

    private fun saveToCache(books: Pair<List<PlannedBook>, List<FinishedBook>>): Completable =
        books.let { (planned, finished) ->
            Completable.concatArray(
                cache.savePlannedBooks(planned),
                cache.saveFinishedBooks(finished)
            )
        }

}