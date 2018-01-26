package com.sayler.ormliteimplementation.day.presenter

import com.sayler.gina.domain.presenter.RxPresenter
import com.sayler.gina.domain.presenter.day.DayContract
import com.sayler.gina.domain.rx.IRxAndroidTransformer
import com.sayler.ormliteimplementation.day.usecase.FindDayByIdUseCase
import com.sayler.ormliteimplementation.day.usecase.FindNextDayAfterDateUseCase
import com.sayler.ormliteimplementation.day.usecase.FindPreviousDayAfterDateUseCase
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.CommunicationError
import com.sayler.ormliteimplementation.exception.OrmLiteError
import org.joda.time.DateTime

/**
 * Created by sayler on 26.01.2018.
 */
class DayPresenter(val findDayByIdUseCase: FindDayByIdUseCase,
                   val findNextDayAfterDateUseCase: FindNextDayAfterDateUseCase,
                   val findPreviousDayAfterDateUseCase: FindPreviousDayAfterDateUseCase,
                   rxAndroidTransformer: IRxAndroidTransformer)
    : RxPresenter<DayContract.View>(rxAndroidTransformer), DayContract.Presenter {
    override fun loadById(id: Long) {
        presenterView?.showProgress()

        findDayByIdUseCase
                .findDayById(id)
                .subscribe(this::onSuccess, this::onError)
    }

    override fun loadNextAfterDate(dateTime: DateTime) {
        presenterView?.showProgress()

        findNextDayAfterDateUseCase
                .findNextDayAfterDateId(dateTime)
                .subscribe(this::onSuccess, this::onErrorNext)
    }

    override fun loadPreviousBeforeDate(dateTime: DateTime) {
        presenterView?.showProgress()

        findPreviousDayAfterDateUseCase
                .findPreviousDayAfterDateId(dateTime)
                .subscribe(this::onSuccess, this::onErrorPrevious)
    }

    private fun onSuccess(list: Day) {
        presenterView?.hideProgress()
        presenterView?.show(list)
    }

    private fun onError(error: Throwable) {
        presenterView?.hideProgress()
        when (error) {
            is CommunicationError.NoDataSource -> presenterView?.noDataSource()
            is OrmLiteError.TimeoutError -> presenterView?.timeout()
            is OrmLiteError.SyntaxError -> presenterView?.syntaxError()
            else -> presenterView?.error()
        }
    }

    private fun onErrorNext(error: Throwable) {
        presenterView?.hideProgress()
        when (error) {
            is OrmLiteError.NoDataError -> presenterView?.noNextItemAvailable()
            else -> onError(error)
        }
    }

    private fun onErrorPrevious(error: Throwable) {
        presenterView?.hideProgress()
        when (error) {
            is OrmLiteError.NoDataError -> presenterView?.noPreviousItemAvailable()
            else -> onError(error)
        }
    }

}