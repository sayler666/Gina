package com.sayler.gina.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.adapter.DaysAdapter;
import com.sayler.gina.domain.DataManager;
import com.sayler.gina.domain.IDay;
import com.sayler.gina.permission.PermissionUtils;
import com.sayler.gina.domain.presenter.diary.DiaryPresenter;
import com.sayler.gina.domain.presenter.diary.DiaryPresenterView;
import com.sayler.gina.ui.UiStateController;
import com.sayler.gina.util.BroadcastReceiverHelper;
import com.sayler.gina.util.Constants;
import com.sayler.gina.util.FileUtils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import rx.android.schedulers.AndroidSchedulers;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity implements DiaryPresenterView, PermissionUtils.PermissionCallback {

  @Inject
  DataManager dataManager;

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

  @Bind(R.id.toolbar)
  Toolbar toolbar;
  @Bind(R.id.pageTitle)
  TextView pageTitle;
  private DaysAdapter daysAdapter;
  private UiStateController uiStateController;
  private BroadcastReceiverHelper broadcastReceiverRefresh;
  private SearchView searchView;

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

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.main_menu, menu);

    setupSearchView(menu);

    return super.onCreateOptionsMenu(menu);
  }

  private void setupSearchView(Menu menu) {
    MenuItem menuItem = menu.findItem(R.id.action_search);

    searchView = (SearchView) menuItem.getActionView();
    searchView.setMaxWidth(Integer.MAX_VALUE);
    View v = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
    v.setBackgroundColor(Color.TRANSPARENT);

    searchView.setOnCloseListener(() -> {
      showPageTitle();
      load();
      return false;
    });

    searchView.setOnSearchClickListener(view -> {
      pageTitle.setVisibility(View.GONE);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    });

    RxSearchView.queryTextChanges(searchView)
        .debounce(1, TimeUnit.SECONDS)
        .filter(charSequence -> charSequence.length() > 0)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(charSequence -> {
          uiStateController.setUiStateLoading();
        })
        .subscribe(this::searchForText);
  }

  private void showPageTitle() {
    pageTitle.setVisibility(View.VISIBLE);
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        showPageTitle();
        clearSearchViewAndHide();
        return true;
      case R.id.file:
        openSourceFileSelectIntent();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void clearSearchViewAndHide() {
    searchView.setQuery("", false);
    searchView.setIconified(true);
  }

  @Override
  public void onBackPressed() {
    if (!searchView.isIconified()) {
      showPageTitle();
      clearSearchViewAndHide();
    } else {
      super.onBackPressed();
    }
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
    setupToolbar();

    setupUiStateController();

    setupRecyclerView();
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(null);
    pageTitle.setText(R.string.app_name);
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

  private void searchForText(CharSequence charSequence) {
    diaryPresenter.loadByTextSearch(String.valueOf(charSequence));
  }

  private void createRecyclerView(List<IDay> items) {
    daysAdapter.setItems(items);
    daysAdapter.notifyDataSetChanged();
  }

  @OnClick(R.id.selectDataSourceButton)
  public void onSelectDataSourceButton() {
    openSourceFileSelectIntent();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    setNewDbFilePath(data.getData().getPath());
  }

  private void openSourceFileSelectIntent() {
    FileUtils.selectFileIntent(this, Constants.REQUEST_CODE_SELECT_DB);
  }

  private void setNewDbFilePath(String path) {
    dataManager.setSourceFile(path);
    load();
  }

  @Override
  public void onDownloaded(List<IDay> data) {
    createRecyclerView(data);
    uiStateController.setUiStateContent();
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
    dataManager.close();
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
    //TODO error handling
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

}
