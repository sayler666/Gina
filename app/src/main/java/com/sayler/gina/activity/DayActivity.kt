package com.sayler.gina.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import butterknife.ButterKnife
import butterknife.OnClick
import com.sayler.gina.GinaApplication
import com.sayler.gina.R
import com.sayler.gina.attachment.AttachmentAdapter
import com.sayler.gina.domain.DataManager
import com.sayler.gina.domain.IAttachment
import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.presenter.diary.DiaryPresenter
import com.sayler.gina.domain.presenter.diary.DiaryPresenterView
import com.sayler.gina.util.BroadcastReceiverHelper
import com.sayler.gina.util.Constants
import com.sayler.gina.util.FileUtils
import kotlinx.android.synthetic.main.a_day.*
import java.io.IOException
import javax.inject.Inject

class DayActivity : BaseActivity(), DiaryPresenterView {
    @Inject
    lateinit var diaryPresenter: DiaryPresenter
    @Inject
    lateinit var dataManager: DataManager<*>

    lateinit var day: IDay
    var dayId: Long = 0

    private lateinit var broadcastReceiverEditDay: BroadcastReceiverHelper
    private lateinit var broadcastReceiverDeleteDay: BroadcastReceiverHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_day)

        ButterKnife.bind(this)
        GinaApplication.dataComponentForActivity(this).inject(this)

        bindPresenters()

        setupBroadcastReceivers()

        readExtras()

        setupViews()

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

    private fun setupViews() {
        //nothing here for now
    }

    private fun bindPresenters() {
        diaryPresenter.onBindView(this)
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
        attachmentsContainer.removeAllViews()
        day.attachments.forEach {
            createAttachmentButton(it).let { button ->
                attachmentsContainer.addView(button)
            }
        }

        //drawer
        val layoutManager = LinearLayoutManager(this)
        attachmentsRecyclerView.layoutManager = layoutManager
        val attachmentAdapter = AttachmentAdapter(day.attachments)
        attachmentsRecyclerView.adapter = attachmentAdapter
    }

    private fun createAttachmentButton(attachment: IAttachment): Button {
        val button = Button(this)
        button.text = attachment.mimeType

        button.setOnClickListener { _ ->
            try {
                FileUtils.openFileIntent(this, attachment.file, attachment.mimeType, applicationContext.packageName + ".provider")
            } catch (e: IOException) {
                e.printStackTrace()
                //TODO error handling
            }
        }
        return button
    }

    @OnClick(R.id.fabEdit)
    fun onFabEditClick() {
        startActivity(DayEditActivity.newIntentEditDay(this, dayId))
    }

    @OnClick(R.id.fabAttachments)
    fun onFabAttachmentsClick() {
        drawer_layout.openDrawer(GravityCompat.END)
    }

    override fun onDownloaded(data: List<IDay>) {
        day = data[0]
        showContent()
    }

    override fun onError(errorMessage: String) {
        //TODO error handling
    }

    override fun onNoDataSource() {
        //TODO error handling
    }

    override fun onPut() {
        //not used
    }

    override fun onDelete() {
        //not used
    }

    override fun onDestroy() {
        diaryPresenter.onUnBindView()
        dataManager.close()
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
