package me.vadik.knigopis

import io.reactivex.Completable
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.model.FinishedBookToSend
import me.vadik.knigopis.model.PlannedBookToSend

interface BookRepository {
    fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable
    fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable
}

class BookRepositoryImpl(
    private val api: Endpoint,
    private val auth: KAuth
) : BookRepository {

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

class BookRepositoryMock : BookRepository {

    override fun saveBook(bookId: String?, book: FinishedBookToSend, done: Boolean?): Completable =
        Completable.fromAction { Thread.sleep(2000) }

    override fun saveBook(bookId: String?, book: PlannedBookToSend, done: Boolean?): Completable =
        Completable.fromAction { Thread.sleep(2000) }
}