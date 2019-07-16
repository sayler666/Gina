package com.sayler.gina.domain.presenter.diary;

import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import com.sayler.gina.domain.interactor.DiaryInteractor;
import com.sayler.gina.domain.presenter.Presenter;

import org.joda.time.DateTime;

import java.util.List;

public class DiaryPresenter extends Presenter<DiaryContract.View> implements DiaryContract.Presenter {

    private static final String TAG = "DummyPresenter";


  /* ------------------------------------------------------ PUBLIC ------------------------------------------------ */

    public DiaryPresenter() {
    }

    @Override
    public void loadById(long id) {

    }

    @Override
    public void loadNextAfterDate(DateTime dateTime) {

    }

    @Override
    public void loadPreviousBeforeDate(DateTime dateTime) {

    }

    @Override
    public void put(IDay day, List<IAttachment> attachments) {

    }

    @Override
    public void delete(IDay day) {

    }

  /* ------------------------------------------------------ PRIVATE ------------------------------------------------ */

}
