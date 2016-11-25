/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.sayler.gina.R;
import com.sayler.gina.model.Dummy;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * TODO Add class description...
 *
 * @author sayler
 */
public class DaysAdapter extends BaseRecyclerViewAdapter<Dummy> implements SectionTitleProvider, StickyRecyclerHeadersAdapter<DaysAdapter.DaysViewHolder> {
  public DaysAdapter(Context context, List<Dummy> items) {
    super(context, items);
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.it_days, parent, false);
    return new DaysViewHolder(view, DaysAdapter.this);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof DaysViewHolder) {
      DaysViewHolder viewHolder = (DaysViewHolder) holder;

      viewHolder.title.setText(items.get(position).title);
    }
  }

  @Override
  public String getSectionTitle(int position) {
    return items.get(position).date;
  }

  @Override
  public long getHeaderId(int position) {
    return position / 10;
  }

  @Override
  public DaysViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
    View view = inflater.inflate(R.layout.it_header, parent, false);
    return new DaysViewHolder(view, DaysAdapter.this);
  }

  @Override
  public void onBindHeaderViewHolder(DaysViewHolder holder, int position) {
    DaysViewHolder viewHolder = holder;
    viewHolder.title.setText(String.valueOf(items.get(position).date));
  }

  public static final class DaysViewHolder extends RecyclerViewHolderWithOnItemClick<Dummy> {

    @Bind(R.id.title)
    public TextView title;

    public DaysViewHolder(final View view, final BaseRecyclerViewAdapter<Dummy> baseRecyclerViewAdapter) {
      super(view, baseRecyclerViewAdapter);
      ButterKnife.bind(this, view);
    }
  }
}
