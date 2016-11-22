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

import java.util.List;

/**
 * TODO Add class description...
 *
 * @author sayler
 */
public class DaysAdapter extends BaseRecyclerViewAdapter<String> implements SectionTitleProvider {
  public DaysAdapter(Context context, List<String> items) {
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

      viewHolder.title.setText(items.get(position));
    }
  }

  @Override
  public String getSectionTitle(int position) {
    return items.get(position);
  }

  public static final class DaysViewHolder extends RecyclerViewHolderWithOnItemClick<String> {

    @Bind(R.id.title)
    public TextView title;

    public DaysViewHolder(final View view, final BaseRecyclerViewAdapter<String> baseRecyclerViewAdapter) {
      super(view, baseRecyclerViewAdapter);
      ButterKnife.bind(this, view);
    }
  }
}
