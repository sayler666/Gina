/**
 * Created by sayler on 2016-11-22.
 * <p>
 */
package com.sayler.ormliteimplementation.interactor;

import com.sayler.monia.domain.IAttachment;
import com.sayler.monia.domain.IDay;
import com.sayler.monia.domain.interactor.BaseInteractor;
import com.sayler.monia.domain.interactor.DaysDeleteInteractorCallback;
import com.sayler.monia.domain.interactor.DaysGetInteractorCallback;
import com.sayler.monia.domain.interactor.DaysGetNextPreviousInteractorCallback;
import com.sayler.monia.domain.interactor.DaysPutInteractorCallback;
import com.sayler.monia.domain.interactor.DiaryInteractor;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;

public class DiaryInteractorOrmLite extends BaseInteractor implements DiaryInteractor {


  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

    public DiaryInteractorOrmLite() {

    }


    @Override
    public void loadDataById(long id, DaysGetInteractorCallback daysGetInteractorCallback) {

    }

    @Override
    public void loadDataNextAfterDate(DateTime dateTime, DaysGetNextPreviousInteractorCallback daysGetNextPreviousInteractorCallback) {

    }

    @Override
    public void loadDataPreviousBeforeDate(DateTime dateTime, DaysGetNextPreviousInteractorCallback daysGetNextPreviousInteractorCallback) {

    }


    @Override
    public void put(IDay day, List<IAttachment> attachments, DaysPutInteractorCallback daysPutInteractorCallback) {
    }

    @Override
    public void delete(IDay day, DaysDeleteInteractorCallback daysDeleteInteractorCallback) {
    }

    @Override
    public List<IDay> getData() {
        return Collections.emptyList();
    }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */
}
