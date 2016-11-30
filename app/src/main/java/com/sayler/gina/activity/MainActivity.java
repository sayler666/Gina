package com.sayler.gina.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.adapter.DaysAdapter;
import com.sayler.gina.permission.PermissionUtils;
import com.sayler.gina.presenter.days.DaysPresenter;
import com.sayler.gina.presenter.days.DaysPresenterView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import entity.Day;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity implements DaysPresenterView, PermissionUtils.PermissionCallback {

  @Inject
  DaysPresenter daysPresenter;

  @Bind(R.id.recyclerView)
  RecyclerView recyclerView;

  @Bind(R.id.fastscroll)
  FastScroller fastScroller;
  private DaysAdapter daysAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_main);
    ButterKnife.bind(this);

    GinaApplication.getDataComponentForActivity(this).inject(this);

    bindPresenters();

    setupViews();

    askFormPermissionAndLoadData();

  }

  private void askFormPermissionAndLoadData() {
    if (!PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      PermissionUtils.askForPermission(this, this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    } else {
      load();
    }
  }

  private void setupViews() {
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

  @OnClick(R.id.fab_add_day)
  public void onFabAddDayClick() {
    startActivity(DayEditActivity.newIntentNewDay(this));
  }

  private void bindPresenters() {
    daysPresenter.onBindView(this);
  }

  private void load() {
    daysPresenter.loadAll();
  }

  @Override
  public void onDownloaded(List<Day> data) {
    createRecyclerView(data);
  }

  private void createRecyclerView(List<Day> items) {
    daysAdapter.setItems(items);
    daysAdapter.notifyDataSetChanged();
  }

  @Override
  public void onError() {
    //TODO
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
  protected void onDestroy() {
    daysPresenter.onUnBindView();
    super.onDestroy();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}
