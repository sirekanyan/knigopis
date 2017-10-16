package me.vadik.knigopis

import io.reactivex.Completable
import me.vadik.knigopis.api.Endpoint
import me.vadik.knigopis.auth.KAuth
import me.vadik.knigopis.model.FinishedBookToSend
import me.vadik.knigopis.model.PlannedBookToSend

interface BookRepository {
  fun saveBook(bookId: String?, book: FinishedBookToSend): Completable
  fun saveBook(bookId: String?, book: PlannedBookToSend): Completable
}

class BookRepositoryImpl(
    private val api: Endpoint,
    private val auth: KAuth
) : BookRepository {

  override fun saveBook(bookId: String?, book: FinishedBookToSend) =
      if (bookId == null) {
        api.postFinishedBook(auth.getAccessToken(), book)
      } else {
        api.putFinishedBook(bookId, auth.getAccessToken(), book)
      }

  override fun saveBook(bookId: String?, book: PlannedBookToSend) =
      if (bookId == null) {
        api.postPlannedBook(auth.getAccessToken(), book)
      } else {
        api.putPlannedBook(bookId, auth.getAccessToken(), book)
      }
}

class BookRepositoryMock : BookRepository {

  override fun saveBook(bookId: String?, book: FinishedBookToSend): Completable =
      Completable.fromAction { Thread.sleep(2000) }

  override fun saveBook(bookId: String?, book: PlannedBookToSend): Completable =
      Completable.fromAction { Thread.sleep(2000) }
}