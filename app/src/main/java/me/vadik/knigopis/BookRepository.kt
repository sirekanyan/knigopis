package me.vadik.knigopis

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.data.BookOrganizer
import me.vadik.knigopis.model.*

interface BookRepository {

    fun loadBooks(): Single<List<Pair<Book, BookHeader>>>

    fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable

    fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable

}

class BookRepositoryImpl(
    private val api: Endpoint,
    private val auth: KAuth,
    private val plannedBookOrganizer: BookOrganizer<PlannedBook>,
    private val finishedBookPrepare: BookOrganizer<FinishedBook>
) : BookRepository {

    override fun loadBooks(): Single<List<Pair<Book, BookHeader>>> =
        Singles.zip(
            api.getPlannedBooks(auth.getAccessToken()),
            api.getFinishedBooks(auth.getAccessToken())
        ).map { (planned, finished) ->
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

}