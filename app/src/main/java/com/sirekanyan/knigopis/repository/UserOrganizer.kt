package com.sirekanyan.knigopis.repository

import com.sirekanyan.knigopis.model.UserModel
import com.sirekanyan.knigopis.model.dto.Subscription
import com.sirekanyan.knigopis.model.toUserModel
import com.sirekanyan.knigopis.repository.UserSorting.*

class UserOrganizer(private val config: Configuration) {

    fun organize(subscriptions: List<Subscription>): List<UserModel> =
        sort(subscriptions.map(Subscription::toUserModel))


    private fun sort(users: List<UserModel>): List<UserModel> =
        when (config.userSorting) {
            DEFAULT -> users
            BY_NAME -> users.sortedBy(UserModel::name)
            BY_COUNT -> {
                users.sortedWith(
                    compareByDescending(UserModel::booksCount)
                        .thenByDescending(UserModel::newBooksCount)
                )
            }
            BY_NEW_COUNT -> {
                users.sortedWith(
                    compareByDescending(UserModel::newBooksCount)
                        .thenByDescending(UserModel::booksCount)
                )
            }
        }

}