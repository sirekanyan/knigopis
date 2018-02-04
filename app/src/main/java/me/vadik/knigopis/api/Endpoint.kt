package me.vadik.knigopis.api

import io.reactivex.Completable
import io.reactivex.Single
import me.vadik.knigopis.adapters.books.UserBook
import me.vadik.knigopis.model.*
import me.vadik.knigopis.model.note.Note
import me.vadik.knigopis.model.subscription.Subscription
import retrofit2.http.*

interface Endpoint {

    @GET("user/get-credentials")
    fun getCredentials(@Query("token") token: String): Single<Credentials>

    @GET("books")
    fun getFinishedBooks(@Query("access-token") accessToken: String): Single<List<FinishedBook>>

    @PUT("books/{id}")
    fun updateFinishedBook(
        @Path("id") id: String,
        @Query("access-token") accessToken: String,
        @Body book: FinishedBookToSend
    ): Completable

    @POST("books")
    fun createFinishedBook(
        @Query("access-token") accessToken: String,
        @Body book: FinishedBookToSend
    ): Completable

    @DELETE("books/{id}")
    fun deleteFinishedBook(
        @Path("id") id: String,
        @Query("access-token") accessToken: String
    ): Completable

    @GET("wishes")
    fun getPlannedBooks(@Query("access-token") accessToken: String): Single<List<PlannedBook>>

    @PUT("wishes/{id}")
    fun updatePlannedBook(
        @Path("id") id: String,
        @Query("access-token") accessToken: String,
        @Body book: PlannedBookToSend
    ): Completable

    @POST("wishes")
    fun createPlannedBook(
        @Query("access-token") accessToken: String,
        @Body book: PlannedBookToSend
    ): Completable

    @DELETE("wishes/{id}")
    fun deletePlannedBook(
        @Path("id") id: String,
        @Query("access-token") accessToken: String
    ): Completable

    @GET("books/latest-notes")
    fun getLatestBooksWithNotes(): Single<Map<String, Note>>

    @GET("subscriptions")
    fun getSubscriptions(
        @Query("access-token") accessToken: String
    ): Single<List<Subscription>>

    @GET("users/{id}/books")
    fun getUserBooks(@Path("id") userId: String): Single<List<UserBook>>

    @POST("subscriptions/{subUserId}")
    fun createSubscription(
        @Path("subUserId") userId: String,
        @Query("access-token") accessToken: String
    ): Single<Any>

    @DELETE("subscriptions/{subUserId}")
    fun deleteSubscription(
        @Path("subUserId") userId: String,
        @Query("access-token") accessToken: String
    ): Single<Any>
}