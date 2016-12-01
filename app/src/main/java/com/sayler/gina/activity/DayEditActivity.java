package com.sayler.gina.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.presenter.days.DaysPresenter;
import com.sayler.gina.presenter.days.DaysPresenterView;
import com.sayler.gina.util.Constatns;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import entity.Day;
import icepick.Icepick;
import icepick.State;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

public class DayEditActivity extends BaseActivity implements DaysPresenterView, DatePickerDialog.OnDateSetListener {

  private static final String TAG = "DayEditActivity";
  @Inject
  DaysPresenter daysPresenter;

  @Bind(R.id.day)
  public TextView dayText;
  @Bind(R.id.year_month)
  public TextView yearMonthText;
  @Bind(R.id.content)
  public EditText contentText;

  @State
  public long dayId;
  @State
  public Day day;

  public static Intent newIntentEditDay(Context context, long dayId) {
    Intent intent = new Intent(context, DayEditActivity.class);
    intent.putExtra(Constatns.DAY_ID, dayId);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_edit_day);
    ButterKnife.bind(this);

    GinaApplication.getDataComponentForActivity(this).inject(this);

    bindPresenters();

    readExtras();

    setupViews();

    load();
  }

  @OnClick({R.id.year_month, R.id.day})
  public void onFabEditClick() {
    DatePickerDialog dpd = DatePickerDialog.newInstance(
        DayEditActivity.this,
        day.getDate().getYear(),
        day.getDate().getMonthOfYear() - 1,
        day.getDate().getDayOfMonth()
    );
    dpd.show(getFragmentManager(), "Datepickerdialog");
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

  private void showContent() {
    dayText.setText(day.getDate().toString(Constatns.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK));
    yearMonthText.setText(day.getDate().toString(Constatns.DATE_PATTERN_YEAR_MONTH));
    contentText.setText(day.getContent());
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

  @Override
  public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
    day.setDate(dateTime);
    showContent();
  }
}
