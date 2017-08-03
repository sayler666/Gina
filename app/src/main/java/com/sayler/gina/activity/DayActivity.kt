package com.sayler.gina.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import butterknife.ButterKnife
import butterknife.OnClick
import com.sayler.gina.GinaApplication
import com.sayler.gina.R
import com.sayler.gina.attachment.AttachmentAdapter
import com.sayler.gina.domain.DataManager
import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.presenter.diary.DiaryContract
import com.sayler.gina.util.BroadcastReceiverHelper
import com.sayler.gina.util.Constants
import com.sayler.gina.util.FileUtils
import kotlinx.android.synthetic.main.a_day.*
import javax.inject.Inject


class DayActivity : BaseActivity() {
    @Inject
    lateinit var diaryPresenter: DiaryContract.Presenter
    @Inject
    lateinit var dataManager: DataManager<*>

    lateinit var day: IDay
    var dayId: Long = 0

    private lateinit var broadcastReceiverEditDay: BroadcastReceiverHelper
    private lateinit var broadcastReceiverDeleteDay: BroadcastReceiverHelper

    private val diaryContractView = object : DiaryContract.View {
        override fun onDownloaded(data: List<IDay>) {
            day = data[0]
            showContent()
        }

        override fun onError(errorMessage: String) {
            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        override fun onNoDataSource() {
            Snackbar.make(findViewById(android.R.id.content), R.string.no_data_source, Snackbar.LENGTH_SHORT).show()
        }

        override fun onPut() {
            //not used
        }

        override fun onDelete() {
            //not used
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
        bindPresenter(diaryPresenter, diaryContractView)
    }

    private fun load() {
        diaryPresenter.loadById(dayId)
    }

    private fun showContent() {
        dayText.text = day.date.toString(Constants.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK)
        yearMonthText.text = day.date.toString(Constants.DATE_PATTERN_YEAR_MONTH)
        content.text = day.content

        showAttachments()
    }

    private fun showAttachments() {
        //drawer
        val layoutManager = LinearLayoutManager(this)
        attachmentsRecyclerView.layoutManager = layoutManager
        val attachmentAdapter = AttachmentAdapter(day.attachments, attachmentsRecyclerView)
        attachmentAdapter.setOnClick({ item, _ ->
            with(item.attachment) {
                FileUtils.openFileIntent(this@DayActivity, file, mimeType, applicationContext.packageName + ".provider")
            }
        })
        attachmentsRecyclerView.adapter = attachmentAdapter
    }

    @OnClick(R.id.fabEdit)
    fun onFabEditClick() {
        startActivity(DayEditActivity.newIntentEditDay(this, dayId))
    }

    @OnClick(R.id.fabAttachments)
    fun onFabAttachmentsClick() {
        drawer_layout.openDrawer(GravityCompat.END)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.END)) {
            drawer_layout.closeDrawer(Gravity.END)
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        (attachmentsRecyclerView.adapter as AttachmentAdapter).releaseMemory()
        dataManager.close()
        System.gc()
        super.onDestroy()
    }

    companion object {

        private val TAG = "DayActivity"

        fun newIntentShowDay(context: Context, dayId: Long): Intent {
            val intent = Intent(context, DayActivity::class.java)
            intent.putExtra(Constants.EXTRA_DAY_ID, dayId)
            return intent
        }
    }

}
