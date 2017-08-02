package com.sayler.gina.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View

abstract class BaseRecyclerViewAdapter<T>(var context: Context, var items: List<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var inflater: LayoutInflater
    var onClickListener: ((item: T, view: View, position: Int) -> Unit)? = null

    init {
        this.context = context
        this.inflater = LayoutInflater.from(context)
    }

    fun setOnClick(onClick: (item: T, view: View, position: Int) -> Unit) {
        onClickListener = onClick
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun onItemClicked(item: T, view: View, position: Int) {
        if (isItemClickable(position)) onClickListener?.invoke(item, view, position)
    }

    private fun isItemClickable(position: Int): Boolean {
        return true
    }

    open class RecyclerViewHolderWithOnItemClick<in S>(view: View, private val adapter: BaseRecyclerViewAdapter<S>) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var item: S? = null
        private var pos: Int = 0

        init {
            this.pos = -1
            view.setOnClickListener(this)
        }

        fun setItem(item: S, position: Int) {
            this.item = item
            this.pos = position
        }

        override fun onClick(v: View) {
            if (item != null && item is S?) {
                adapter.onItemClicked(item as S, v, pos)
            }
        }
    }
}
