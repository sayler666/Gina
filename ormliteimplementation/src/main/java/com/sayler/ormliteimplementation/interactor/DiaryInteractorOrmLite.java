/**
 * Created by sayler on 2016-11-22.
 * <p>
 */
package com.sayler.ormliteimplementation.interactor;

import com.sayler.gina.domain.DataManager;
import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import com.sayler.gina.domain.interactor.BaseInteractor;
import com.sayler.gina.domain.interactor.DaysDeleteInteractorCallback;
import com.sayler.gina.domain.interactor.DaysGetInteractorCallback;
import com.sayler.gina.domain.interactor.DaysGetNextPreviousInteractorCallback;
import com.sayler.gina.domain.interactor.DaysPutInteractorCallback;
import com.sayler.gina.domain.interactor.DiaryInteractor;
import com.sayler.gina.domain.rx.IRxAndroidTransformer;
import com.sayler.ormliteimplementation.AttachmentsDataProvider;
import com.sayler.ormliteimplementation.DaysDataProvider;

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
