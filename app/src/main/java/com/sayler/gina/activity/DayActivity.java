package com.sayler.gina.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.presenter.days.DaysPresenter;
import com.sayler.gina.presenter.days.DaysPresenterView;
import com.sayler.gina.util.Constatns;
import entity.Day;
import icepick.Icepick;
import icepick.State;

import javax.inject.Inject;
import java.util.List;

public class DayActivity extends BaseActivity implements DaysPresenterView {

  private static final String TAG = "DayActivity";
  @Inject
  DaysPresenter daysPresenter;

  @Bind(R.id.content)
  public TextView contentText;
  @Bind(R.id.day)
  public TextView dayText;
  @Bind(R.id.year_month)
  public TextView yearMonthText;
  @Bind(R.id.fab_edit)
  public FloatingActionButton fabEdit;

  @State
  public long dayId;
  @State
  public Day day;

  public static Intent newIntentShowDay(Context context, long dayId) {
    Intent intent = new Intent(context, DayActivity.class);
    intent.putExtra(Constatns.DAY_ID, dayId);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_day);
    ButterKnife.bind(this);

    GinaApplication.getDataComponentForActivity(this).inject(this);

    bindPresenters();

    readExtras();

    setupViews();

    load();
  }

  private void readExtras() {
    if (getIntent().hasExtra(Constatns.DAY_ID)) {
      dayId = getIntent().getLongExtra(Constatns.DAY_ID, -1);
    }
  }

  private void setupViews() {

  }

  private void bindPresenters() {
    daysPresenter.onBindView(this);
  }

  private void load() {
    daysPresenter.loadById(dayId);
  }

  private void showContent() {
    dayText.setText(day.getDate().toString(Constatns.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK));
    yearMonthText.setText(day.getDate().toString(Constatns.DATE_PATTERN_YEAR_MONTH));
    contentText.setText(day.getContent());
  }


  @OnClick(R.id.fab_edit)
  public void onFabEditClick() {
    startActivity(DayEditActivity.newIntentEditDay(this, dayId));
  }

  @Override
  public void onError(String errorMessage) {
    //TODO
  }

  @Override
  public void onDownloaded(List<Day> data) {
    day = data.get(0);
    showContent();
  }

  @Override
  public void onNoDataSource() {
    //TODO
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    Icepick.restoreInstanceState(this, savedInstanceState);
  }

  @Override
  protected void onDestroy() {
    daysPresenter.onUnBindView();
    super.onDestroy();
  }

}
