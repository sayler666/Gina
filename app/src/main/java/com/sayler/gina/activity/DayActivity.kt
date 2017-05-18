package com.sayler.gina.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import butterknife.ButterKnife
import butterknife.OnClick
import com.annimon.stream.Stream
import com.sayler.gina.GinaApplication
import com.sayler.gina.R
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
import kotlin.reflect.KClass

class DayActivity : BaseActivity(), DiaryPresenterView {
    @Inject
    internal lateinit var diaryPresenter: DiaryPresenter
    @Inject
    internal lateinit var dataManager: DataManager<*>

    var dayId: Long = 0
    var dayData: IDay? = null

    private var broadcastReceiverEditDay: BroadcastReceiverHelper? = null
    private var broadcastReceiverDeleteDay: BroadcastReceiverHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_day)
        ButterKnife.bind(this)

        GinaApplication.getDataComponentForActivity(this).inject(this)

        bindPresenters()

        setupBroadcastReceivers()

        readExtras()

        setupViews()

        load()
    }

    private fun setupBroadcastReceivers() {
        broadcastReceiverEditDay = BroadcastReceiverHelper( { this.load() })
        broadcastReceiverEditDay!!.register(this, IntentFilter(Constants.BROADCAST_EDIT_DAY))

        broadcastReceiverDeleteDay = BroadcastReceiverHelper( { this.finish() })
        broadcastReceiverDeleteDay!!.register(this, IntentFilter(Constants.BROADCAST_DELETE_DAY))
    }

    override fun onResume() {
        super.onResume()
        broadcastReceiverEditDay!!.callScheduledAction()
        broadcastReceiverDeleteDay!!.callScheduledAction()
    }

    private fun readExtras() {
        if (intent.hasExtra(Constants.EXTRA_DAY_ID)) {
            dayId = intent.getLongExtra(Constants.EXTRA_DAY_ID, -1)
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
        day.text = dayData?.date?.toString(Constants.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK)
        year_month.text = dayData?.date?.toString(Constants.DATE_PATTERN_YEAR_MONTH)
        content.text = dayData?.content

        showAttachments()
    }

    private fun showAttachments() {
        attachmentsContainer!!.removeAllViews()
        val iterator = dayData?.attachments?.iterator()
        Stream.of(iterator).forEach { attachment ->
            val button = createAttachmentButton(attachment)
            attachmentsContainer!!.addView(button)
        }

    }

    private fun createAttachmentButton(attachment: IAttachment): Button {
        val button = Button(this)
        button.text = attachment.mimeType

        button.setOnClickListener { view ->
            try {
                FileUtils.openFileIntent(this, attachment.file, attachment.mimeType, applicationContext.packageName + ".provider")
            } catch (e: IOException) {
                e.printStackTrace()
                //TODO error handling
            }
        }
        return button
    }

    @OnClick(R.id.fab_edit)
    fun onFabEditClick() {
        startActivity(DayEditActivity.newIntentEditDay(this, dayId))
    }

    override fun onDownloaded(data: List<IDay>) {
        dayData = data[0]
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
            val intent = Intent(context,DayActivity::class.java)
            intent.putExtra(Constants.EXTRA_DAY_ID, dayId)
            return intent
        }
    }

}

