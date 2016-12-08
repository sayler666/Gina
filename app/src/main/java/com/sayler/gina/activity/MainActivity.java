package com.sayler.gina.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.sayler.domain.dao.DBManager;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.adapter.DaysAdapter;
import com.sayler.gina.permission.PermissionUtils;
import com.sayler.gina.presenter.days.DaysPresenterView;
import com.sayler.gina.presenter.days.DiaryPresenter;
import com.sayler.gina.ui.RevealHelper;
import com.sayler.gina.ui.UiStateController;
import com.sayler.gina.util.BroadcastReceiverHelper;
import com.sayler.gina.util.Constants;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import entity.Day;
import rx.Observable;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity implements DaysPresenterView, PermissionUtils.PermissionCallback {

  @Inject
  DBManager dbManager;

  @Inject
  DiaryPresenter diaryPresenter;

  @Bind(R.id.recyclerView)
  RecyclerView recyclerView;

  @Bind(R.id.fastscroll)
  FastScroller fastScroller;

  @Bind(R.id.root)
  View root;

  @Bind(R.id.content)
  View content;

  @Bind(R.id.progressBar)
  View progressBar;

  @Bind(R.id.error)
  View error;

  @Bind(R.id.noDataSource)
  View noDataSource;

  @Bind(R.id.noDataSourceText)
  TextView noDataSourceText;

  @Bind(R.id.errorText)
  TextView errorText;
  private DaysAdapter daysAdapter;
  private UiStateController uiStateController;
  private BroadcastReceiverHelper broadcastReceiverRefresh;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_main);
    ButterKnife.bind(this);

    GinaApplication.getDataComponentForActivity(this).inject(this);

    bindPresenters();

    setupBroadcastReceivers();

    setupViews();

    askFormPermissionAndLoadData();

  }

  private void setupBroadcastReceivers() {
    broadcastReceiverRefresh = new BroadcastReceiverHelper(this::load);
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Constants.BROADCAST_EDIT_DAY);
    intentFilter.addAction(Constants.BROADCAST_DELETE_DAY);
    broadcastReceiverRefresh.register(this, intentFilter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    broadcastReceiverRefresh.callScheduledAction();
  }

  private void askFormPermissionAndLoadData() {
    if (!PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      PermissionUtils.askForPermission(this, this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    } else {
      load();
    }
  }

  private void setupViews() {
    setupUiStateController();

    setupRecyclerView();
  }

  private void setupUiStateController() {
    uiStateController = new UiStateController.Builder()
        .withContentUi(content)
        .withLoadingUi(progressBar)
        .withErrorUi(error)
        .withEmptyUi(noDataSource)
        .build();
  }

  private void setupRecyclerView() {
    //recycler view
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    daysAdapter = new DaysAdapter(this, Collections.emptyList());
    recyclerView.setAdapter(daysAdapter);

    //sticky header
    StickyRecyclerHeadersDecoration decor = new StickyRecyclerHeadersDecoration(daysAdapter);
    recyclerView.addItemDecoration(decor);
    recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.divider).marginResId(R.dimen.p_medium).build());
    fastScroller.setRecyclerView(recyclerView);

    daysAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override
      public void onChanged() {
        decor.invalidateHeaders();
      }
    });

    //on item click
    daysAdapter.setOnItemClickListener((item, view, position) -> {
      Intent intent = DayActivity.newIntentShowDay(this, item.getId());
      //shared elements
      View dayText = view.findViewById(R.id.day);
      Pair<View, String> pair1 = Pair.create(dayText, dayText.getTransitionName());

      //noinspection unchecked - unable to check
      ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1);
      this.startActivity(intent, options.toBundle());
    });
  }

  @OnClick(R.id.fab)
  public void onFabAddDayClick() {
    startActivity(DayEditActivity.newIntentNewDay(this));
  }

  private void bindPresenters() {
    diaryPresenter.onBindView(this);
  }

  private void load() {
    uiStateController.setUiStateLoading();
    diaryPresenter.loadAll();
  }

  private void createRecyclerView(List<Day> items) {
    daysAdapter.setItems(items);
    daysAdapter.notifyDataSetChanged();
  }

  @OnClick(R.id.selectDataSourceButton)
  public void rebindDB() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_DB);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    selectDbFile(data.getData().getPath());
  }

  private void selectDbFile(String path) {
    dbManager.setDatabasePath(path);
    dbManager.rebindProviders();
    load();
  }

  @Override
  public void onDownloaded(List<Day> data) {
    createRecyclerView(data);
    uiStateController.setUiStateContent();
    Observable
        .just(1)
        .delay(100, TimeUnit.MILLISECONDS)
        .subscribe(integer -> new RevealHelper().reveal(content, root));

  }

  @Override
  public void onNoDataSource() {
    uiStateController.setUiStateEmpty();
  }

  @Override
  public void onError(String s) {
    uiStateController.setUiStateError();
    errorText.setText(s);
  }

  @Override
  protected void onDestroy() {
    diaryPresenter.onUnBindView();
    super.onDestroy();
  }

  @Override
  public void onDelete() {
    //not used
  }

  @Override
  public void onPut() {
    //not used
  }

  @Override
  public void onPermissionGranted(String permission) {
    load();
  }

  @Override
  public void onPermissionRejected(String permission) {
    //TODO
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

}
