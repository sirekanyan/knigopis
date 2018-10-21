package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.extensions.io2main
import io.reactivex.Completable

interface AuthRepository {
    fun isAuthorized(): Boolean
    fun saveToken(token: String)
    fun authorize(): Completable
}

class AuthRepositoryImpl(
    private val api: Endpoint,
    private val storage: TokenStorage
) : AuthRepository {

    override fun isAuthorized(): Boolean =
        storage.accessToken != null

    override fun saveToken(token: String) {
        storage.token = token
    }

    override fun authorize(): Completable {
        val token = storage.token
        return if (token == null || isAuthorized()) {
            Completable.complete()
        } else {
            api.getCredentials(token)
                .io2main()
                .doOnSuccess {
                    storage.accessToken = it.accessToken
                }
                .ignoreElement()
        }
    }

}