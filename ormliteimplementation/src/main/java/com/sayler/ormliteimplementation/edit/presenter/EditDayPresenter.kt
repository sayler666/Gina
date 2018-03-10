package com.sayler.ormliteimplementation.edit.presenter

import com.sayler.gina.domain.IAttachment
import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.presenter.RxPresenter
import com.sayler.gina.domain.presenter.edit.EditDayContract
import com.sayler.gina.domain.presenter.list.usecase.CheckIfRememberedSourceUseCase
import com.sayler.gina.domain.rx.IRxAndroidTransformer
import com.sayler.ormliteimplementation.day.usecase.FindDayByIdUseCase
import com.sayler.ormliteimplementation.edit.usecase.DeleteDayUseCase
import com.sayler.ormliteimplementation.edit.usecase.PutDayAndAttachmentUseCase
import com.sayler.ormliteimplementation.entity.Day
import com.sayler.ormliteimplementation.exception.CommunicationError
import com.sayler.ormliteimplementation.exception.OrmLiteError

/**
 * Created by sayler on 26.01.2018.
 */
class EditDayPresenter(private val findDayByIdUseCase: FindDayByIdUseCase,
                       private val deleteDateUseUseCase: DeleteDayUseCase,
                       private val putDayAndAttachmentUseCase: PutDayAndAttachmentUseCase,
                       private val checkIfRememberedSourceUseCase: CheckIfRememberedSourceUseCase,
                       rxAndroidTransformer: IRxAndroidTransformer)
    : RxPresenter<EditDayContract.View>(rxAndroidTransformer), EditDayContract.Presenter {

    override fun onCreate() {
        //TODO setup edit mode
        //check if any file opened if new day
    }

    override fun loadById(id: Long) {
        presenterView?.showProgress()

        findDayByIdUseCase
                .findDayById(id)
                .subscribe(this::onSuccess, this::onError)
    }

    override fun put(day: IDay, attachments: List<IAttachment>) {
        presenterView?.showProgress()
        attachments
                .filter {
                    it.file.size > 2000000
                }
                .forEach {
                    presenterView?.attachmentTooBig()
                    return
                }

        putDayAndAttachmentUseCase
                .put(day as Day, attachments)
                .subscribe({ onSuccessPut() }, this::onError)
    }

    override fun delete(day: IDay) {
        presenterView?.showProgress()
        deleteDateUseUseCase
                .delete(day.id)
                .subscribe({ onSuccessDelete() }, this::onError)
    }

    private fun onSuccessPut() {
        presenterView?.hideProgress()
        presenterView?.put()
    }

    private fun onSuccessDelete() {
        presenterView?.hideProgress()
        presenterView?.delete()
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

}