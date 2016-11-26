package com.sayler.gina.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.adapter.DaysAdapter;
import com.sayler.gina.permission.PermissionUtils;
import com.sayler.gina.presenter.dummy.DaysPresenter;
import com.sayler.gina.presenter.dummy.DaysPresenterView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import entity.Day;

import javax.inject.Inject;
import java.util.List;

public class MainActivity extends BaseActivity implements DaysPresenterView, PermissionUtils.PermissionCallback {

  @Inject
  DaysPresenter daysPresenter;

  @Bind(R.id.recyclerView)
  RecyclerView recyclerView;

  @Bind(R.id.fastscroll)
  FastScroller fastScroller;
  private LinearLayoutManager layoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_main);
    ButterKnife.bind(this);

    GinaApplication.getDataComponentForActivity(this).inject(this);

    bindPresenters();

    setupViews();

    if (!PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      PermissionUtils.askForPermission(this, this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    } else {
      load();
    }

  }

  private void setupViews() {
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
  }

  private void bindPresenters() {
    daysPresenter.onBindView(this);
  }

  private void load() {
    daysPresenter.download();
  }

  @Override
  public void onDownloaded(List<Day> data) {
    createRecyclerView(data);
  }

  private void createRecyclerView(List<Day> strings) {
    DaysAdapter daysAdapter = new DaysAdapter(this, strings);
    recyclerView.setAdapter(daysAdapter);
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

  }

  @Override
  public void onNoInternet() {
    //not used
  }

  @Override
  public void onServerError() {
    //not used
  }

  @Override
  protected void onDestroy() {
    daysPresenter.onUnBindView();
    super.onDestroy();
  }

  @Override
  public void onPermissionGranted(String permission) {
    load();
  }

  @Override
  public void onPermissionRejected(String permission) {

  }
}
