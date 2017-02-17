package com.sayler.gina.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.annimon.stream.Stream;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.domain.DataManager;
import com.sayler.gina.domain.IAttachment;
import com.sayler.gina.domain.IDay;
import com.sayler.gina.domain.presenter.diary.DiaryPresenter;
import com.sayler.gina.domain.presenter.diary.DiaryPresenterView;
import com.sayler.gina.util.BroadcastReceiverHelper;
import com.sayler.gina.util.Constants;
import com.sayler.gina.util.FileUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class DayActivity extends BaseActivity implements DiaryPresenterView {

  private static final String TAG = "DayActivity";
  @Inject
  DiaryPresenter diaryPresenter;
  @Inject
  DataManager dataManager;

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

  public long dayId;
  public IDay day;

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
    Iterator<? extends IAttachment> iterator = day.getAttachments().iterator();
    Stream.of(iterator).forEach(attachment -> {
      Button button = createAttachmentButton(attachment);
      attachmentsContainer.addView(button);
    });

  }

  @NonNull
  private Button createAttachmentButton(IAttachment attachment) {
    Button button = new Button(this);
    button.setText(attachment.getMimeType());

    button.setOnClickListener(view -> {
      try {
        FileUtils.openFileIntent(this, attachment.getFile(), attachment.getMimeType(), getApplicationContext().getPackageName() + ".provider");
      } catch (IOException e) {
        e.printStackTrace();
        //TODO error handling
      }
    });
    return button;
  }

  @OnClick(R.id.fab_edit)
  public void onFabEditClick() {
    startActivity(DayEditActivity.newIntentEditDay(this, dayId));
  }

  @Override
  public void onDownloaded(List<IDay> data) {
    day = data.get(0);
    showContent();
  }

  @Override
  public void onError(String errorMessage) {
    //TODO error handling
  }

  @Override
  public void onNoDataSource() {
    //TODO error handling
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
  protected void onDestroy() {
    diaryPresenter.onUnBindView();
    dataManager.close();
    super.onDestroy();
  }

}
