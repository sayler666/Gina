package com.sayler.gina.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.presenter.days.DaysPresenterView;
import com.sayler.gina.presenter.days.DiaryPresenter;
import com.sayler.gina.util.BroadcastReceiverHelper;
import com.sayler.gina.util.Constants;
import entity.Attachment;
import entity.Day;
import icepick.Icepick;
import icepick.State;
import okio.BufferedSink;
import okio.Okio;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DayActivity extends BaseActivity implements DaysPresenterView {

  private static final String TAG = "DayActivity";
  @Inject
  DiaryPresenter diaryPresenter;

  @Bind(R.id.content)
  public TextView contentText;
  @Bind(R.id.day)
  public TextView dayText;
  @Bind(R.id.year_month)
  public TextView yearMonthText;
  @Bind(R.id.fab_edit)
  public FloatingActionButton fabEdit;
  @Bind(R.id.attachmentsContainer)
  public ViewGroup attachmentsContainer;

  @State
  public long dayId;
  @State
  public Day day;

  private BroadcastReceiverHelper broadcastReceiverEditDay;
  private BroadcastReceiverHelper broadcastReceiverDeleteDay;

  public static Intent newIntentShowDay(Context context, long dayId) {
    Intent intent = new Intent(context, DayActivity.class);
    intent.putExtra(Constants.EXTRA_DAY_ID, dayId);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_day);
    ButterKnife.bind(this);

    GinaApplication.getDataComponentForActivity(this).inject(this);

    bindPresenters();

    setupBroadcastReceivers();

    readExtras();

    setupViews();

    load();
  }

  private void setupBroadcastReceivers() {
    broadcastReceiverEditDay = new BroadcastReceiverHelper(this::load);
    broadcastReceiverEditDay.register(this, new IntentFilter(Constants.BROADCAST_EDIT_DAY));

    broadcastReceiverDeleteDay = new BroadcastReceiverHelper(this::finish);
    broadcastReceiverDeleteDay.register(this, new IntentFilter(Constants.BROADCAST_DELETE_DAY));
  }

  @Override
  protected void onResume() {
    super.onResume();
    broadcastReceiverEditDay.callScheduledAction();
    broadcastReceiverDeleteDay.callScheduledAction();
  }

  private void readExtras() {
    if (getIntent().hasExtra(Constants.EXTRA_DAY_ID)) {
      dayId = getIntent().getLongExtra(Constants.EXTRA_DAY_ID, -1);
    }
  }

  private void setupViews() {
    //nothing here for now
  }

  private void bindPresenters() {
    diaryPresenter.onBindView(this);
  }

  private void load() {
    diaryPresenter.loadById(dayId);
  }

  private void showContent() {
    dayText.setText(day.getDate().toString(Constants.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK));
    yearMonthText.setText(day.getDate().toString(Constants.DATE_PATTERN_YEAR_MONTH));
    contentText.setText(day.getContent());

    showAttachments();
  }

  private void showAttachments() {
    attachmentsContainer.removeAllViews();

    //TODO make it sexy
    for (Attachment attachment : day.getAttatchments()) {
      Button button = new Button(this);
      button.setText(attachment.getMimeType());

      button.setOnClickListener(view -> {
        //save file
        File file = new File(this.getFilesDir() + File.separator + "shared_file");
        BufferedSink sink = null;
        try {
          sink = Okio.buffer(Okio.sink(file));
          sink.write(attachment.getFile());
          sink.close();

          Uri fileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
          Intent intent = ShareCompat.IntentBuilder.from(this)
              .setStream(fileUri)
              .setType(attachment.getMimeType())
              .getIntent()
              .setAction(Intent.ACTION_VIEW)
              .setDataAndType(fileUri, attachment.getMimeType())
              .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          startActivity(intent);
        } catch (IOException e) {
          e.printStackTrace();
          //TODO show error
        }

      });

      attachmentsContainer.addView(button);
    }
  }

  @OnClick(R.id.fab_edit)
  public void onFabEditClick() {
    startActivity(DayEditActivity.newIntentEditDay(this, dayId));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Constants.REQUEST_CODE_EDIT_DAY) {
      diaryPresenter.loadById(dayId);
    }
  }

  @Override
  public void onDownloaded(List<Day> data) {
    day = data.get(0);
    showContent();
  }

  @Override
  public void onError(String errorMessage) {
    //TODO
  }

  @Override
  public void onNoDataSource() {
    //TODO
  }

  @Override
  public void onPut() {
    //not used
  }

  @Override
  public void onDelete() {
    //not used
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
    diaryPresenter.onUnBindView();
    super.onDestroy();
  }

}
