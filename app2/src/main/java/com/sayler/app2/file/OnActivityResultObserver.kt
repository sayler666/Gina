package com.sayler.app2.file

import android.content.Intent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

data class Result(val requestCode: Int, val resultCode: Int, val data: Intent?)

@Singleton
class OnActivityResultObserver @Inject constructor() {

    private val subject = PublishSubject.create<Result>()

    fun observe(requestCode: Int): Observable<Result> = subject.filter {
        it.requestCode == requestCode
    }

    fun publish(requestCode: Int, resultCode: Int, data: Intent?) {
        subject.onNext(Result(requestCode, resultCode, data))
    }
}
