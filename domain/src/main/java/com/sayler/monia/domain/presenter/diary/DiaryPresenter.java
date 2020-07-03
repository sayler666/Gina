package com.sayler.monia.domain.presenter.diary;

import com.sayler.monia.domain.IAttachment;
import com.sayler.monia.domain.IDay;
import com.sayler.monia.domain.presenter.Presenter;

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
