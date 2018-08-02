package com.sirekanyan.knigopis.common

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<V : Any>(private vararg val nestedPresenters: Presenter) {

    lateinit var view: V
    private val disposables = CompositeDisposable()

    fun <T> Flowable<T>.bind(onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) {
        disposables.add(subscribe(onSuccess, onError))
    }

    fun <T> Observable<T>.bind(onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) {
        disposables.add(subscribe(onSuccess, onError))
    }

    fun <T> Single<T>.bind(onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) {
        disposables.add(subscribe(onSuccess, onError))
    }

    fun Completable.bind(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        disposables.add(subscribe(onSuccess, onError))
    }

    fun stop() {
        nestedPresenters.forEach(Presenter::stop)
        disposables.clear()
    }

}