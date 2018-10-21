package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.model.dto.*
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface Endpoint {

    @GET("user/get-credentials")
    fun getCredentials(@Query("token") token: String): Single<Credentials>

    @GET("books")
    fun getFinishedBooks(): Single<List<FinishedBook>>

    @PUT("books/{id}")
    fun updateFinishedBook(@Path("id") id: String, @Body book: FinishedBookToSend): Completable

    @POST("books")
    fun createFinishedBook(@Body book: FinishedBookToSend): Completable

    @DELETE("books/{id}")
    fun deleteFinishedBook(@Path("id") id: String): Completable

    @GET("wishes")
    fun getPlannedBooks(): Single<List<PlannedBook>>

    @PUT("wishes/{id}")
    fun updatePlannedBook(@Path("id") id: String, @Body book: PlannedBookToSend): Completable

    @POST("wishes")
    fun createPlannedBook(@Body book: PlannedBookToSend): Completable

    @DELETE("wishes/{id}")
    fun deletePlannedBook(@Path("id") id: String): Completable

    @GET("books/latest-notes")
    fun getLatestBooksWithNotes(): Single<Map<String, Note>>

    @GET("subscriptions")
    fun getSubscriptions(): Single<List<Subscription>>

    @GET("users/current")
    fun getProfile(): Single<User>

    @GET("users/{id}/books")
    fun getUserBooks(@Path("id") userId: String): Single<List<FinishedBook>>

    @GET("users/{id}")
    fun getUser(@Path("id") userId: String): Single<User>

    @PUT("users/{id}")
    fun updateProfile(@Path("id") userId: String, @Body profile: Profile): Completable

    @POST("subscriptions/{subUserId}")
    fun createSubscription(@Path("subUserId") userId: String): Completable

    @DELETE("subscriptions/{subUserId}")
    fun deleteSubscription(@Path("subUserId") userId: String): Completable

}