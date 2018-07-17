package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.NetworkChecker
import com.sirekanyan.knigopis.model.UserModel
import com.sirekanyan.knigopis.model.toUserModel
import com.sirekanyan.knigopis.repository.api.Endpoint
import com.sirekanyan.knigopis.repository.cache.CacheKey
import com.sirekanyan.knigopis.repository.cache.CommonCache
import com.sirekanyan.knigopis.repository.cache.genericType
import com.sirekanyan.knigopis.repository.common.CommonRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface UserRepository {

    fun observeUsers(): Flowable<List<UserModel>>

}

class UserRepositoryImpl(
    private val api: Endpoint,
    private val cache: CommonCache,
    private val auth: KAuth,
    networkChecker: NetworkChecker
) : CommonRepository<List<UserModel>>(networkChecker),
    UserRepository {

    override fun observeUsers() = observe()

    override fun loadFromNetwork(): Single<List<UserModel>> =
        api.getSubscriptions(auth.getAccessToken()).map { it.map { it.toUserModel() } }

    override fun findCached(): Maybe<List<UserModel>> =
        cache.getFromJson(CacheKey.USERS, genericType<List<UserModel>>())

    override fun saveToCache(data: List<UserModel>): Completable =
        cache.saveToJson(CacheKey.USERS, data)

}