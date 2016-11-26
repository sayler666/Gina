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
public class DaysAdapter extends BaseRecyclerViewAdapter<Day> implements SectionTitleProvider, StickyRecyclerHeadersAdapter<DaysAdapter.DaysHeaderViewHolder> {
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

      //content
      String contentShort;
      String contentFull = items.get(position).getContent();
      if (contentFull.length() > 150) {
        contentShort = contentFull.substring(0, 150) + " (...)";
      } else {
        contentShort = contentFull;
      }

      //date
      String date = items.get(position).getDate().toString("d, EEEE");

      viewHolder.content.setText(contentShort);
      viewHolder.time.setText(date);

      if (viewHolder.expanded) {
        viewHolder.expanded = false;
      }

      viewHolder.time.setOnClickListener(view -> {
        if (!viewHolder.expanded) {
          viewHolder.content.setText(contentFull);
        } else {
          viewHolder.content.setText(contentShort);
        }
        viewHolder.expanded = !viewHolder.expanded;
      });
    }
  }

  @Override
  public String getSectionTitle(int position) {
    return items.get(position).getDate().toString("YYYY \nMMMM");
  }

  @Override
  public long getHeaderId(int position) {
    return Math.abs(items.get(position).getDate().toString().substring(0, 7).hashCode());
  }

  @Override
  public DaysHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
    View view = inflater.inflate(R.layout.it_header, parent, false);
    return new DaysHeaderViewHolder(view, DaysAdapter.this);
  }

  @Override
  public void onBindHeaderViewHolder(DaysHeaderViewHolder holder, int position) {
    holder.title.setText(items.get(position).getDate().toString("YYYY MMMM"));
  }

  static final class DaysViewHolder extends RecyclerViewHolderWithOnItemClick<Day> {

    @Bind(R.id.content)
    public TextView content;
    @Bind(R.id.time)
    public TextView time;
    public boolean expanded = false;

    DaysViewHolder(final View view, final BaseRecyclerViewAdapter<Day> baseRecyclerViewAdapter) {
      super(view, baseRecyclerViewAdapter);
      ButterKnife.bind(this, view);
    }

  }

  static final class DaysHeaderViewHolder extends RecyclerViewHolderWithOnItemClick<Day> {

    @Bind(R.id.content)
    public TextView title;

    DaysHeaderViewHolder(final View view, final BaseRecyclerViewAdapter<Day> baseRecyclerViewAdapter) {
      super(view, baseRecyclerViewAdapter);
      ButterKnife.bind(this, view);
    }
  }
}
