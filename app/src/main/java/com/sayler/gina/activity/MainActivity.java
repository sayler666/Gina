package com.sayler.gina.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.sayler.gina.GinaApplication;
import com.sayler.gina.R;
import com.sayler.gina.adapter.DaysAdapter;
import com.sayler.gina.presenter.dummy.DummyPresenter;
import com.sayler.gina.presenter.dummy.IDummyPresenterView;
import com.sayler.gina.model.Dummy;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import javax.inject.Inject;
import java.util.List;

public class MainActivity extends BaseActivity implements IDummyPresenterView {

  @Inject
  DummyPresenter dummyPresenter;

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

    load();
  }

  private void setupViews() {
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
  }

  private void bindPresenters() {
    dummyPresenter.onBindView(this);
  }

  private void load() {
    dummyPresenter.download();
  }

  @Override
  public void onDownloaded(List<Dummy> data) {
    createRecyclerView(data);
  }

  private void createRecyclerView(List<Dummy> strings) {
    DaysAdapter daysAdapter = new DaysAdapter(this, strings);
    recyclerView.setAdapter(daysAdapter);
    StickyRecyclerHeadersDecoration decor = new StickyRecyclerHeadersDecoration(daysAdapter);
    recyclerView.addItemDecoration(decor);
    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()));
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
    dummyPresenter.onUnBindView();
    super.onDestroy();
  }
}
