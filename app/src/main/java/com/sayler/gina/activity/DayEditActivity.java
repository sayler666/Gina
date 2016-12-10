package com.sayler.gina.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.annimon.stream.Stream;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.IAttachment;
import com.sayler.gina.IDay;
import com.sayler.gina.R;
import com.sayler.gina.interactor.days.ObjectCreator;
import com.sayler.gina.presenter.diary.DiaryPresenter;
import com.sayler.gina.presenter.diary.DiaryPresenterView;
import com.sayler.gina.util.Constants;
import com.sayler.gina.util.FileUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import icepick.Icepick;
import org.joda.time.DateTime;
import realm.DataManager;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DayEditActivity extends BaseActivity implements DiaryPresenterView, DatePickerDialog.OnDateSetListener {

  private static final String TAG = "DayEditActivity";
  @Inject
  DiaryPresenter diaryPresenter;
  @Inject
  ObjectCreator objectCreator;
  @Inject
  DataManager dataManager;

  public Long dayId = -1L;
  public IDay day;

  @Bind(R.id.day)
  public TextView dayText;
  @Bind(R.id.year_month)
  public TextView yearMonthText;
  @Bind(R.id.content)
  public EditText contentText;
  @Bind(R.id.attachmentsContainer)
  public ViewGroup attachmentsContainer;

  private EditMode editMode;
  private AttachmentsManager attachmentsManager;

  private enum EditMode {
    NEW_DAY, EDIT_DAY

  }

  private class AttachmentsManager {
    private HashMap<IAttachment, Button> tmpAttachmentButtonHashMap = new HashMap<>();

    private ViewGroup attachmentsContainer;

    public AttachmentsManager(ViewGroup attachmentsContainer) {
      this.attachmentsContainer = attachmentsContainer;
    }

    public void addFile(byte[] bytes, String mimeType) {
      IAttachment newAttachment = objectCreator.createAttachment();
      newAttachment.setFile(bytes);
      newAttachment.setMimeType(mimeType);

      Button newButton = new Button(attachmentsContainer.getContext());
      newButton.setText(mimeType);
      newButton.setOnClickListener(view -> {
            tmpAttachmentButtonHashMap.remove(newAttachment);
            attachmentsContainer.removeView(view);
          }
      );

      attachmentsContainer.addView(newButton);
      tmpAttachmentButtonHashMap.put(newAttachment, newButton);
    }

    public List<IAttachment> returnAttachments() {
      List<IAttachment> attachments = new ArrayList<>();
      Stream.of(tmpAttachmentButtonHashMap).forEach(attachmentButtonEntry -> {
        attachments.add(attachmentButtonEntry.getKey());
      });

      return attachments;
    }

  }

  public static Intent newIntentEditDay(Context context, long dayId) {
    Intent intent = new Intent(context, DayEditActivity.class);
    intent.putExtra(Constants.EXTRA_DAY_ID, dayId);
    intent.putExtra(Constants.EXTRA_EDIT_MODE, EditMode.EDIT_DAY.ordinal());
    return intent;
  }

  public static Intent newIntentNewDay(Context context) {
    Intent intent = new Intent(context, DayEditActivity.class);
    intent.putExtra(Constants.EXTRA_EDIT_MODE, EditMode.NEW_DAY.ordinal());
    return intent;
  }

  public static void sendEditDayBroadcast(Context context) {
    Intent intent = new Intent(Constants.BROADCAST_EDIT_DAY);
    context.sendBroadcast(intent);
  }

  public static void sendDeleteDayBroadcast(Context context) {
    Intent intent = new Intent(Constants.BROADCAST_DELETE_DAY);
    context.sendBroadcast(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_edit_day);
    ButterKnife.bind(this);

    GinaApplication.getDataComponentForActivity(this).inject(this);

    attachmentsManager = new AttachmentsManager(attachmentsContainer);

    bindPresenters();

    readExtras();

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
    dpd.show(getFragmentManager(), DatePickerDialog.class.getCanonicalName());
  }

  private void readExtras() {
    if (getIntent().hasExtra(Constants.EXTRA_EDIT_MODE)) {
      //get edit mode
      int editModeOrdinal = getIntent().getExtras().getInt(Constants.EXTRA_EDIT_MODE);
      editMode = EditMode.values()[editModeOrdinal];

      switch (editMode) {
        case NEW_DAY:
          day = objectCreator.createDay();
          day.setDate(new DateTime());
          break;
        case EDIT_DAY:
          dayId = getIntent().getLongExtra(Constants.EXTRA_DAY_ID, -1);
          break;
      }
    }
  }

  private void bindPresenters() {
    diaryPresenter.onBindView(this);
  }

  private void load() {
    switch (editMode) {
      case NEW_DAY:
        showTextContent();
        break;
      case EDIT_DAY:
        diaryPresenter.loadById(dayId);
        break;
    }
  }

  @OnClick(R.id.fab_save)
  public void onFabSaveClick() {
    put();
  }

  @OnClick(R.id.fab_delete)
  public void onFabDeleteClick() {
    delete();
  }

  @OnClick(R.id.fab_add_attachment)
  public void onFabAddAttachmentClick() {
    FileUtils.selectFileIntent(this, Constants.REQUEST_CODE_SELECT_ATTACHMENT);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Constants.REQUEST_CODE_SELECT_ATTACHMENT) {
      addAttachment(data);
    }
  }

  private void put() {
    day.setContent(contentText.getText().toString());

    diaryPresenter.put(day, attachmentsManager.returnAttachments());
  }

  private void delete() {
    diaryPresenter.delete(day);
  }

  @Override
  public void onPut() {
    sendEditDayBroadcast(this);
    dataManager.close();
    finish();
  }

  @Override
  public void onDownloaded(List<IDay> data) {
    day = data.get(0);
    showTextContent();
    showAttachments();
  }

  @Override
  public void onDelete() {
    sendDeleteDayBroadcast(this);
    dataManager.close();
    finish();
  }

  @Override
  public void onNoDataSource() {
    //TODO error handling
  }

  @Override
  public void onError(String errorMessage) {
    showError(errorMessage);
  }

  private void showError(String errorMessage) {
    Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
  }

  private void showTextContent() {
    dayText.setText(day.getDate().toString(Constants.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK));
    yearMonthText.setText(day.getDate().toString(Constants.DATE_PATTERN_YEAR_MONTH));
    contentText.setText(day.getContent());
  }

  private void showAttachments() {
    for (IAttachment attachment : day.getAttachments()) {
      attachmentsManager.addFile(attachment.getFile(), attachment.getMimeType());
    }
  }

  private void addAttachment(Intent data) {
    try {
      byte[] fileBytes = FileUtils.readFileFromUri(data.getData(), this);
      String mimeType = FileUtils.readMimeTypeFromUri(data.getData(), this);

      attachmentsManager.addFile(fileBytes, mimeType);
    } catch (IOException e) {
      e.printStackTrace();
      //TODO error handling
    }
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
    dataManager.close();
    super.onDestroy();
  }

  @Override
  public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
    day.setDate(dateTime);
    day.setContent(contentText.getText().toString());
    showTextContent();
  }
}
