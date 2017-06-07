package com.sayler.gina.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.ViewGroup
import android.widget.Button
import butterknife.ButterKnife
import butterknife.OnClick
import com.annimon.stream.Stream
import com.sayler.gina.GinaApplication
import com.sayler.gina.R
import com.sayler.gina.domain.DataManager
import com.sayler.gina.domain.IAttachment
import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.ObjectCreator
import com.sayler.gina.domain.presenter.diary.DiaryPresenter
import com.sayler.gina.domain.presenter.diary.DiaryPresenterView
import com.sayler.gina.util.Constants
import com.sayler.gina.util.FileUtils
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.a_edit_day.*
import org.joda.time.DateTime
import java.io.IOException
import java.util.*
import javax.inject.Inject

class DayEditActivity : BaseActivity(), DiaryPresenterView, DatePickerDialog.OnDateSetListener {
    @Inject
    lateinit var diaryPresenter: DiaryPresenter
    @Inject
    lateinit var objectCreator: ObjectCreator
    @Inject
    lateinit var dataManager: DataManager<*>

    private lateinit var day: IDay
    private lateinit var attachmentsManager: AttachmentsManager
    private lateinit var editMode: EditMode
    private var dayId: Long = -1L

    private enum class EditMode {
        NEW_DAY, EDIT_DAY
    }

    private inner class AttachmentsManager(private val attachmentsContainer: ViewGroup) {
        private val tmpAttachmentButtonHashMap = HashMap<IAttachment, Button>()

        fun addFile(bytes: ByteArray, mimeType: String) {
            val newAttachment = objectCreator.createAttachment()
            newAttachment.file = bytes
            newAttachment.mimeType = mimeType

            val newButton = Button(attachmentsContainer.context)
            newButton.text = mimeType
            newButton.setOnClickListener { view ->
                tmpAttachmentButtonHashMap.remove(newAttachment)
                attachmentsContainer.removeView(view)
            }

            attachmentsContainer.addView(newButton)
            tmpAttachmentButtonHashMap.put(newAttachment, newButton)
        }

        fun returnAttachments(): List<IAttachment> {
            val attachments = ArrayList<IAttachment>()
            Stream.of(tmpAttachmentButtonHashMap).forEach { attachmentButtonEntry -> attachments.add(attachmentButtonEntry.key) }

            return attachments
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_edit_day)

        ButterKnife.bind(this)
        GinaApplication.dataComponentForActivity(this).inject(this)

        attachmentsManager = AttachmentsManager(attachmentsContainer)

        bindPresenters()

        readExtras()

        load()
    }

    private fun readExtras() {
        if (intent.hasExtra(Constants.EXTRA_EDIT_MODE)) {
            //get edit mode
            val editModeOrdinal = intent.extras.getInt(Constants.EXTRA_EDIT_MODE)
            editMode = EditMode.values()[editModeOrdinal]

            when (editMode) {
                DayEditActivity.EditMode.NEW_DAY -> {
                    day = objectCreator.createDay()
                    day.date = DateTime()
                }
                DayEditActivity.EditMode.EDIT_DAY -> dayId = intent.getLongExtra(Constants.EXTRA_DAY_ID, -1)
            }
        } else {
            throw IllegalStateException("Missing intent extra params: EXTRA_EDIT_MODE")
        }
    }

    private fun bindPresenters() {
        diaryPresenter.onBindView(this)
    }

    private fun load() {
        when (editMode) {
            DayEditActivity.EditMode.NEW_DAY -> showTextContent()
            DayEditActivity.EditMode.EDIT_DAY -> diaryPresenter.loadById(dayId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_SELECT_ATTACHMENT) {

            //multiple files
            if (data.clipData != null) {
                for (i in 0..data.clipData.itemCount - 1)
                    addAttachment(data.clipData.getItemAt(i).uri)
            } else {
                addAttachment(data.data)
            }//single file

        }
    }

    @OnClick(R.id.yearMonthText, R.id.dayText)
    fun onFabEditClick() {
        val dpd = DatePickerDialog.newInstance(
                this@DayEditActivity,
                day.date.year,
                day.date.monthOfYear - 1,
                day.date.dayOfMonth
        )
        dpd.show(fragmentManager, DatePickerDialog::class.java.canonicalName)
    }

    @OnClick(R.id.fab_save)
    fun onFabSaveClick() {
        put()
    }

    @OnClick(R.id.fab_delete)
    fun onFabDeleteClick() {
        delete()
    }

    @OnClick(R.id.fab_add_attachment)
    fun onFabAddAttachmentClick() {
        FileUtils.selectFileIntent(this, Constants.REQUEST_CODE_SELECT_ATTACHMENT)
    }

    private fun put() {
        day.content = content!!.text.toString()

        diaryPresenter.put(day, attachmentsManager.returnAttachments())
    }

    private fun delete() {
        diaryPresenter.delete(day)
    }

    override fun onPut() {
        sendEditDayBroadcast(this)
        dataManager.close()
        finish()
    }

    override fun onDownloaded(data: List<IDay>) {
        day = data[0]
        showTextContent()
        showAttachments()
    }

    override fun onDelete() {
        sendDeleteDayBroadcast(this)
        dataManager.close()
        finish()
    }

    override fun onNoDataSource() {
        //TODO error handling
    }

    override fun onError(errorMessage: String) {
        showError(errorMessage)
    }

    private fun showError(errorMessage: String) {
        Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show()
    }

    private fun showTextContent() {
        dayText.text = day.date.toString(Constants.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK)
        yearMonthText.text = day.date.toString(Constants.DATE_PATTERN_YEAR_MONTH)
        content.setText(day.content)
        if (content.text.isNotEmpty())
            content.setSelection(content.text.length - 1)
    }

    private fun showAttachments() {
        for (attachment in day.attachments) {
            attachmentsManager.addFile(attachment.file, attachment.mimeType)
        }
    }

    private fun addAttachment(uri: Uri) {
        try {
            val fileBytes = FileUtils.readFileFromUri(uri, this)
            val mimeType = FileUtils.readMimeTypeFromUri(uri, this)

            attachmentsManager.addFile(fileBytes, mimeType)
        } catch (e: IOException) {
            e.printStackTrace()
            //TODO error handling
        }

    }

    override fun onDestroy() {
        diaryPresenter.onUnBindView()
        dataManager.close()
        super.onDestroy()
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val dateTime = DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0)
        day.date = dateTime
        day.content = content.text.toString()
        showTextContent()
    }

    companion object {

        fun newIntentEditDay(context: Context, dayId: Long): Intent {
            val intent = Intent(context, DayEditActivity::class.java)
            intent.putExtra(Constants.EXTRA_DAY_ID, dayId)
            intent.putExtra(Constants.EXTRA_EDIT_MODE, EditMode.EDIT_DAY.ordinal)
            return intent
        }

        fun newIntentNewDay(context: Context): Intent {
            val intent = Intent(context, DayEditActivity::class.java)
            intent.putExtra(Constants.EXTRA_EDIT_MODE, EditMode.NEW_DAY.ordinal)
            return intent
        }

        fun sendEditDayBroadcast(context: Context) {
            val intent = Intent(Constants.BROADCAST_EDIT_DAY)
            context.sendBroadcast(intent)
        }

        fun sendDeleteDayBroadcast(context: Context) {
            val intent = Intent(Constants.BROADCAST_DELETE_DAY)
            context.sendBroadcast(intent)
        }
    }
}
