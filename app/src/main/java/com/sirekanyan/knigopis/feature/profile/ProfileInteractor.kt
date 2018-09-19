package com.sirekanyan.knigopis.feature.profile

import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.dto.Profile
import com.sirekanyan.knigopis.model.dto.User
import com.sirekanyan.knigopis.repository.AuthRepository
import com.sirekanyan.knigopis.repository.BookRepository
import com.sirekanyan.knigopis.repository.Endpoint
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit

interface ProfileInteractor {

    fun getProfile(): Single<User>
    fun getBooks(): Observable<BookDataModel>
    fun updateProfile(user: User, nickname: String): Completable
    fun logout()

}

class ProfileInteractorImpl(
    private val api: Endpoint,
    private val bookRepository: BookRepository,
    private val authRepository: AuthRepository
) : ProfileInteractor {

    override fun getProfile(): Single<User> =
        api.getProfile()
            .io2main()

    override fun getBooks(): Observable<BookDataModel> =
        bookRepository.findCached()
            .toSingle(listOf())
            .map { it.filterIsInstance<BookDataModel>() }
            .map { it.shuffled() }
            .flatMapObservable {
                Observables.zip(
                    Observable.fromIterable(it),
                    Observable.interval(5, TimeUnit.MILLISECONDS)
                )
            }
            .map { (book) -> book }
            .io2main()

    override fun updateProfile(user: User, nickname: String): Completable =
        api.updateProfile(user.id, Profile(nickname, user.fixedProfile))
            .io2main()

    override fun logout() {
        authRepository.clear()
    }

}