package com.sayler.gina.domain.presenter.diary;

import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import com.sayler.gina.domain.presenter.BasePresenter;
import com.sayler.gina.domain.presenter.BaseView;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by sayler on 03.08.2017.
 */

public class DiaryContract {
  public interface View extends BaseView {
    void onError(String error);

    void onDownloaded(List<IDay> dayList);

    void onPut();

    void onDelete();
  }

  public interface Presenter extends BasePresenter<DiaryContract.View> {
 
    void loadById(long id);

    void loadNextAfterDate(DateTime dateTime);

    void loadPreviousBeforeDate(DateTime dateTime);

    void put(IDay day, List<IAttachment> attachments);

    void delete(IDay day);
  }

}
