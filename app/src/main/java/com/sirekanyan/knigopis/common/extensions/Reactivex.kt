package com.sirekanyan.knigopis.common.extensions

import com.sirekanyan.knigopis.feature.ProgressView
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.io2main(): Single<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.io2main(): Flowable<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.io2main(): Observable<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.io2main(): Completable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.showProgressBar(progress: ProgressView): Flowable<T> =
    doOnSubscribe {
        progress.showProgress()
    }.doOnNext {
        progress.hideProgress()
    }.doFinally {
        progress.hideProgress()
        progress.hideSwipeRefresh()
    }