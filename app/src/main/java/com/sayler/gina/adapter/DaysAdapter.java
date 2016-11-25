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
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import entity.Day;

import java.util.List;

/**
 * @author sayler
 */
public class DaysAdapter extends BaseRecyclerViewAdapter<Day> implements SectionTitleProvider, StickyRecyclerHeadersAdapter<DaysAdapter.DaysViewHolder> {
  public DaysAdapter(Context context, List<Day> items) {
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

      String content = items.get(position).getContent();
      if (content.length() > 150) {
        content = content.substring(0, 150) + " (...)";
      }

      viewHolder.title.setText(content);
    }
  }

  @Override
  public String getSectionTitle(int position) {
    return items.get(position).getDate().substring(0, 7);
  }

  @Override
  public long getHeaderId(int position) {
    return Math.abs(items.get(position).getDate().substring(0, 7).hashCode());
  }

  @Override
  public DaysViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
    View view = inflater.inflate(R.layout.it_header, parent, false);
    return new DaysViewHolder(view, DaysAdapter.this);
  }

  @Override
  public void onBindHeaderViewHolder(DaysViewHolder holder, int position) {
    holder.title.setText(items.get(position).getDate().substring(0, 7));
  }

  static final class DaysViewHolder extends RecyclerViewHolderWithOnItemClick<Day> {

    @Bind(R.id.title)
    public TextView title;

    DaysViewHolder(final View view, final BaseRecyclerViewAdapter<Day> baseRecyclerViewAdapter) {
      super(view, baseRecyclerViewAdapter);
      ButterKnife.bind(this, view);
    }
  }
}
