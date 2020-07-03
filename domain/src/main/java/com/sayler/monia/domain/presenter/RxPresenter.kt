package com.sayler.monia.domain.presenter

import com.sayler.monia.domain.rx.IRxAndroidTransformer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxPresenter<T>(val rxAndroidTransformer: IRxAndroidTransformer) : BasePresenter<T> {
    protected var presenterView: T? = null

    private val compositeDisposable = CompositeDisposable()

    override fun bindView(iPresenterView: T) {
        presenterView = iPresenterView
    }

    override fun unbindView() {
        freeResources()
    }

    private fun freeResources() {
        presenterView = null
        unsubscribeAll()
    }

    protected fun unsubscribeAll() {
        compositeDisposable.clear()
    }

    protected fun needToUnsubscribe(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun <T> Observable<T>.subscribe(success: (T) -> Unit, error: (Throwable) -> Unit) {
        val disposable = this
                .compose(rxAndroidTransformer.applySchedulers())
                .subscribe(success, error)

        needToUnsubscribe(disposable)
    }

}
