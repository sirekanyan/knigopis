package com.sirekanyan.knigopis.feature.profile

import com.sirekanyan.knigopis.common.extensions.io2main
import com.sirekanyan.knigopis.model.BookDataModel
import com.sirekanyan.knigopis.model.ProfileModel
import com.sirekanyan.knigopis.model.dto.User
import com.sirekanyan.knigopis.model.toProfile
import com.sirekanyan.knigopis.model.toProfileModel
import com.sirekanyan.knigopis.repository.BookRepository
import com.sirekanyan.knigopis.repository.Endpoint
import com.sirekanyan.knigopis.repository.TokenStorage
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit

interface ProfileInteractor {

    fun getProfile(): Single<ProfileModel>
    fun getBooks(): Observable<BookDataModel>
    fun updateProfile(profile: ProfileModel): Completable
    fun logout()

}

class ProfileInteractorImpl(
    private val api: Endpoint,
    private val bookRepository: BookRepository,
    private val tokenStorage: TokenStorage
) : ProfileInteractor {

    override fun getProfile(): Single<ProfileModel> =
        api.getProfile()
            .map(User::toProfileModel)
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

    override fun updateProfile(profile: ProfileModel): Completable =
        api.updateProfile(profile.id, profile.toProfile())
            .io2main()

    override fun logout() {
        tokenStorage.clearTokens()
    }

}