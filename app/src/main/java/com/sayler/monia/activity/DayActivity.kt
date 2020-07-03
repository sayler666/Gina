package com.sayler.monia.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import com.sayler.monia.GinaApplication
import com.sayler.monia.R
import com.sayler.monia.attachment.AttachmentAdapter
import com.sayler.monia.domain.IDay
import com.sayler.monia.domain.presenter.day.DayContract
import com.sayler.monia.util.BroadcastReceiverHelper
import com.sayler.monia.util.Constants
import com.sayler.monia.util.FileUtils
import kotlinx.android.synthetic.main.a_day.*
import javax.inject.Inject


class DayActivity : BaseActivity() {
    @Inject
    lateinit var dayPresenter: DayContract.Presenter

    lateinit var day: IDay
    var dayId: Long = 0

    private lateinit var broadcastReceiverEditDay: BroadcastReceiverHelper
    private lateinit var broadcastReceiverDeleteDay: BroadcastReceiverHelper

    private val dayView = object : DayContract.View {
        override fun showProgress() {
            //not used
        }

        override fun hideProgress() {
            //not used
        }

        override fun noDataSource() {
            Snackbar.make(findViewById(R.id.coordinator), "No data source", Snackbar.LENGTH_SHORT).show()
        }

        override fun show(day: IDay) {
            this@DayActivity.day = day
            this@DayActivity.dayId = day.id
            showContent()
        }

        override fun noPreviousItemAvailable() {
            Snackbar.make(findViewById(R.id.coordinator), "No previous day available", Snackbar.LENGTH_SHORT).show()
        }

        override fun noNextItemAvailable() {
            Snackbar.make(findViewById(R.id.coordinator), "No next day available", Snackbar.LENGTH_SHORT).show()
        }

        override fun timeout() {
            Snackbar.make(findViewById(R.id.coordinator), "Timeout error", Snackbar.LENGTH_SHORT).show()
        }

        override fun syntaxError() {
            Snackbar.make(findViewById(R.id.coordinator), "Syntax error", Snackbar.LENGTH_SHORT).show()
        }

        override fun error() {
            Snackbar.make(findViewById(R.id.coordinator), "Error", Snackbar.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_day)

        ButterKnife.bind(this)
        GinaApplication.dataComponentForActivity(this).inject(this)

        bindPresenters()

        setupBroadcastReceivers()

        readExtras()

        load()
    }

    private fun setupBroadcastReceivers() {
        broadcastReceiverEditDay = BroadcastReceiverHelper { load() }
        broadcastReceiverEditDay.register(this, IntentFilter(Constants.BROADCAST_EDIT_DAY))

        broadcastReceiverDeleteDay = BroadcastReceiverHelper { finish() }
        broadcastReceiverDeleteDay.register(this, IntentFilter(Constants.BROADCAST_DELETE_DAY))
    }

    override fun onResume() {
        super.onResume()
        broadcastReceiverEditDay.callScheduledAction()
        broadcastReceiverDeleteDay.callScheduledAction()
    }

    private fun readExtras() {
        if (intent.hasExtra(Constants.EXTRA_DAY_ID)) {
            dayId = intent.getLongExtra(Constants.EXTRA_DAY_ID, -1)
        } else {
            throw IllegalStateException("Missing intent extra params: EXTRA_DAY_ID")
        }
    }

    private fun bindPresenters() {
        bindPresenter(dayPresenter, dayView)
    }

    private fun load() {
        dayPresenter.loadById(dayId)
    }

    private fun showContent() {
        with(day) {
            dayText.text = date.toString(Constants.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK)
            yearMonthText.text = date.toString(Constants.DATE_PATTERN_YEAR_MONTH)
            contentText.text = content
        }
        showAttachments()
    }

    private fun showAttachments() {
        with(day) {
            if (attachments.isEmpty()) {
                fabAttachments.visibility = View.GONE
            } else {
                fabAttachments.visibility = View.VISIBLE
            }
            //drawer
            val layoutManager = LinearLayoutManager(this@DayActivity)
            attachmentsRecyclerView.layoutManager = layoutManager
            val attachmentAdapter = AttachmentAdapter(attachments, attachmentsRecyclerView)
            attachmentAdapter.setOnClick({ item, _ ->
                with(item.attachment) {
                    FileUtils.openFileIntent(this@DayActivity, file, mimeType, applicationContext.packageName + ".provider")
                }
            })
            attachmentsRecyclerView.adapter = attachmentAdapter
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                onFabPreviousDayClick()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                onFabNextDayClick()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @OnClick(R.id.fabEdit)
    fun onFabEditClick() {
        startActivity(EditDayActivity.newIntentEditDay(this, dayId))
    }

    @OnClick(R.id.fabAttachments)
    fun onFabAttachmentsClick() {
        drawer_layout.openDrawer(GravityCompat.END)
    }

    @OnClick(R.id.fabNextDay)
    fun onFabNextDayClick() {
        dayPresenter.loadNextAfterDate(day.date)
    }

    @OnClick(R.id.fabPreviousDay)
    fun onFabPreviousDayClick() {
        dayPresenter.loadPreviousBeforeDate(day.date)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.END)) {
            drawer_layout.closeDrawer(Gravity.END)
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        if (attachmentsRecyclerView.adapter != null)
            (attachmentsRecyclerView.adapter as AttachmentAdapter).releaseMemory()
        System.gc()
        super.onDestroy()
    }

    companion object {

        fun newIntentShowDay(context: Context, dayId: Long): Intent {
            val intent = Intent(context, DayActivity::class.java)
            intent.putExtra(Constants.EXTRA_DAY_ID, dayId)
            return intent
        }
    }

}
