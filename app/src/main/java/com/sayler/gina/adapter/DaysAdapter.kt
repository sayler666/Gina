/**
 * Created by sayler on 2016-11-22.
 *
 *
 */
package com.sayler.gina.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.sayler.gina.R
import com.sayler.gina.domain.IDay
import com.sayler.gina.ui.truncateTo
import com.sayler.gina.util.Constants
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter

/**
 * @author sayler
 */
class DaysAdapter(context: Context, items: List<IDay>) : BaseRecyclerViewAdapter<IDay>(context, items), SectionTitleProvider, StickyRecyclerHeadersAdapter<DaysAdapter.DaysHeaderViewHolder> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.it_days, parent, false)
        return DaysViewHolder(view, this@DaysAdapter)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DaysViewHolder) {
            with(holder) {
                val day = items[position]
                setItem(day, position)
                //attachments icon
                day.attachments.size.let { attachmentsCount ->
                    attachmentsCountView.visibility = if (attachmentsCount > 0) {
                        attachmentsCountView.text = attachmentsCount.toString()
                        View.VISIBLE
                    } else
                        View.GONE
                }

                //content
                val contentFull = day.content
                val contentShort = contentFull?.truncateTo(200, "...")
                contentShortView.text = contentShort
                contentView.text = contentFull

                //date
                day.date.toString(Constants.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK).let { date ->
                    timeView.text = date
                }

                //expand on time click
                timeView.setOnClickListener { _ ->
                    if (expanded) showShortView() else showFullView()
                }
                showShortView()
            }
        }
    }

    override fun getSectionTitle(position: Int): String {
        return items[position].date.toString(Constants.DATE_PATTERN_YEAR_MONTH_2_LINES)
    }

    override fun getHeaderId(position: Int): Long {
        return Math.abs(items[position].date.toString().substring(0, 7).hashCode()).toLong()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): DaysHeaderViewHolder {
        val view = inflater.inflate(R.layout.it_header, parent, false)
        return DaysHeaderViewHolder(view, this@DaysAdapter)
    }

    override fun onBindHeaderViewHolder(holder: DaysHeaderViewHolder, position: Int) {
        holder.title.text = items[position].date.toString(Constants.DATE_PATTERN_YEAR_MONTH)
    }

    private fun DaysViewHolder.showFullView() {
        contentShortView.visibility = View.GONE
        contentView.visibility = View.VISIBLE
        expanded = true
    }

    private fun DaysViewHolder.showShortView() {
        contentShortView.visibility = View.VISIBLE
        contentView.visibility = View.GONE
        expanded = false
    }

    class DaysViewHolder(view: View, baseRecyclerViewAdapter: BaseRecyclerViewAdapter<IDay>) : BaseRecyclerViewAdapter.RecyclerViewHolderWithOnItemClick<IDay>(view, baseRecyclerViewAdapter) {
        @Bind(R.id.contentText)
        lateinit var contentView: TextView
        @Bind(R.id.contentShort)
        lateinit var contentShortView: TextView
        @Bind(R.id.day)
        lateinit var timeView: TextView
        @Bind(R.id.attachmentsCount)
        lateinit var attachmentsCountView: TextView
        var expanded = false

        init {
            ButterKnife.bind(this, view)
        }
    }

    class DaysHeaderViewHolder(view: View, baseRecyclerViewAdapter: BaseRecyclerViewAdapter<IDay>) : BaseRecyclerViewAdapter.RecyclerViewHolderWithOnItemClick<IDay>(view, baseRecyclerViewAdapter) {
        @Bind(R.id.contentText)
        lateinit var title: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
