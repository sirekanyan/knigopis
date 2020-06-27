package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.common.android.NetworkChecker
import com.sirekanyan.knigopis.model.UserModel
import com.sirekanyan.knigopis.repository.cache.CacheKey
import com.sirekanyan.knigopis.repository.cache.CommonCache
import com.sirekanyan.knigopis.repository.cache.genericType
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
    private val organizer: UserOrganizer,
    networkChecker: NetworkChecker
) : CommonRepository<List<UserModel>>(networkChecker),
    UserRepository {

    override fun observeUsers() = observe()

    override fun loadFromNetwork(): Single<List<UserModel>> =
        api.getSubscriptions().map(organizer::organize)

    override fun findCached(): Maybe<List<UserModel>> =
        cache.find(CacheKey.USERS, genericType<List<UserModel>>())

    override fun saveToCache(data: List<UserModel>): Completable =
        cache.save(CacheKey.USERS, data)

}