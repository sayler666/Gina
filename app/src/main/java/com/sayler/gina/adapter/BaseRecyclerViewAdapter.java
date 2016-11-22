package com.sayler.gina.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;


public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  protected OnItemClickListener<T> onItemClickListener;
  protected List<T> items;
  protected LayoutInflater inflater;
  protected Context context;

  public BaseRecyclerViewAdapter(final Context context, final List<T> items) {
    this.context = context;
    this.items = items;
    this.inflater = LayoutInflater.from(context);
  }



  public void setItems(List<T> items) {
    this.items = items;
    notifyDataSetChanged();
  }

  @Override
  public long getItemId(final int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  private void onItemClicked(final T item, final int position) {
    if (onItemClickListener != null && isItemClickable(1)) {
      onItemClickListener.onItemClicked(item, position);
    }
  }

  public Context getContext() {
    return context;
  }

  protected boolean isItemClickable(int position) {
    return true;
  }

  public void setOnItemClickListener(final OnItemClickListener<T> onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public interface OnItemClickListener<T> {
    void onItemClicked(T item, int position);
  }

  public static class RecyclerViewHolderWithOnItemClick<S> extends RecyclerView.ViewHolder implements View.OnClickListener {
    private S item;
    private int position;
    private BaseRecyclerViewAdapter<S> adapter;

    public RecyclerViewHolderWithOnItemClick(final View view, final BaseRecyclerViewAdapter<S> adapter) {
      super(view);
      this.adapter = adapter;
      this.position = -1;
      view.setOnClickListener(this);
    }

    public void setItem(final S item, final int position) {
      this.item = item;
      this.position = position;
    }

    @Override
    public void onClick(final View v) {
      adapter.onItemClicked(item, position);
    }
  }
}
