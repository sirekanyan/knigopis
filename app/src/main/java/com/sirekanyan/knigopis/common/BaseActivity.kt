package com.sirekanyan.knigopis.common

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    fun <T> Single<T>.bind(onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) {
        disposables.add(subscribe(onSuccess, onError))
    }

    fun Completable.bind(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        disposables.add(subscribe(onSuccess, onError))
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

}