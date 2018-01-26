package com.sayler.gina.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTouch
import com.jakewharton.rxbinding2.widget.textChanges
import com.sayler.gina.GinaApplication
import com.sayler.gina.R
import com.sayler.gina.attachment.AttachmentAdapter
import com.sayler.gina.attachment.AttachmentManagerContract
import com.sayler.gina.domain.DataManager
import com.sayler.gina.domain.IAttachment
import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.ObjectCreator
import com.sayler.gina.domain.presenter.diary.DiaryContract
import com.sayler.gina.util.AlertUtility
import com.sayler.gina.util.Constants
import com.sayler.gina.util.FileUtils
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.a_day_edit.*
import org.joda.time.DateTime
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DayEditActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {
    @Inject
    lateinit var diaryPresenter: DiaryContract.Presenter
    @Inject
    lateinit var objectCreator: ObjectCreator
    @Inject
    lateinit var dataManager: DataManager<*>
    @Inject
    lateinit var attachmentManager: AttachmentManagerContract.Presenter

    private lateinit var day: IDay
    private lateinit var editMode: EditMode
    private var dayId: Long = -1L
    private var initialHashDay: Int = -1
    private var initialHashAttachments: Int = -1
    private lateinit var fabs: List<FloatingActionButton>
    private lateinit var attachmentAdapter: AttachmentAdapter

    private enum class EditMode {
        NEW_DAY, EDIT_DAY
    }

    private val diaryContractView = object : DiaryContract.View {
        override fun showProgress() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun hideProgress() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun noDataSource() {
            Snackbar.make(findViewById(android.R.id.content), R.string.no_data_source, Snackbar.LENGTH_SHORT).show()
        }

        override fun onPut() {
            sendEditDayBroadcast(this@DayEditActivity)
            dataManager.close()
            finish()
        }

        override fun onDownloaded(data: List<IDay>) {
            day = data[0]
            initialHashDay = day.hashCode()
            initialHashAttachments = day.attachments.hashCode()
            setupDay()
        }

        override fun onDelete() {
            sendDeleteDayBroadcast(this@DayEditActivity)
            dataManager.close()
            finish()
        }

        override fun onError(errorMessage: String) {
            showError(errorMessage)
        }
    }

    private val attachmentManagerView = object : AttachmentManagerContract.View {
        override fun showProgress() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun hideProgress() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun noDataSource() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onUpdate(attachments: MutableCollection<IAttachment>) {
            attachmentAdapter.updateItems(attachments)
            attachmentAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_day_edit)

        fabs = listOf(fab_add_attachment, fab_delete, fab_save, fabAttachments)

        ButterKnife.bind(this)
        GinaApplication.dataComponentForActivity(this).inject(this)

        bindPresenters()

        readExtras()

        setupViews()

        load()
    }

    private fun setupViews() {
        content.textChanges()
                .skip(2)
                .map { day.content = content.text.toString() }
                .doOnNext { fabs.onEach { it.hide() } }
                .buffer(2, TimeUnit.SECONDS)
                .filter { t -> t.size <= 0 }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { fabs.onEach { it.show() } }
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
                    initialHashDay = day.hashCode()
                    initialHashAttachments = day.attachments.hashCode()
                }
                DayEditActivity.EditMode.EDIT_DAY -> dayId = intent.getLongExtra(Constants.EXTRA_DAY_ID, -1)
            }
        } else {
            throw IllegalStateException("Missing intent extra params: EXTRA_EDIT_MODE")
        }
    }

    private fun bindPresenters() {
        bindPresenter(attachmentManager, attachmentManagerView)
        bindPresenter(diaryPresenter, diaryContractView)
    }


    private fun load() {
        when (editMode) {
            DayEditActivity.EditMode.NEW_DAY -> setupDay()
            DayEditActivity.EditMode.EDIT_DAY -> diaryPresenter.loadById(dayId)
        }
    }

    private fun setupDay() {
        showTextContent()
        setupAttachments()
    }

    private fun showTextContent() {
        dayText.text = day.date.toString(Constants.DATA_PATTERN_DAY_NUMBER_DAY_OF_WEEK)
        yearMonthText.text = day.date.toString(Constants.DATE_PATTERN_YEAR_MONTH)
        content.setText(day.content)
        if (content.text.isNotEmpty())
            content.setSelection(content.text.length - 1)
    }

    private fun setupAttachments() {
        //setup drawer with attachment
        val layoutManager = LinearLayoutManager(this)
        attachmentsRecyclerView.layoutManager = layoutManager
        attachmentAdapter = AttachmentAdapter(day.attachments, attachmentsRecyclerView, true)
        //on item click
        attachmentAdapter.setOnClick({ item, _ ->
            with(item.attachment) {
                FileUtils.openFileIntent(this@DayEditActivity, file, mimeType, applicationContext.packageName + ".provider")
            }
        })
        //on remove button click on item
        attachmentAdapter.setOnRemoveClick { item ->
            AlertUtility.showConfirmationAlert(this, R.string.remove_attachment_title, R.string.remove_attachment_confirmation, DialogInterface.OnClickListener { dialogInterface, i ->
                attachmentManager.remove(item.attachment)
            })
        }
        attachmentsRecyclerView.adapter = attachmentAdapter

        //setup attachmentManager
        attachmentManager.setup(day.attachments)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED && requestCode == Constants.REQUEST_CODE_SELECT_ATTACHMENT && data != null) {
            //multiple files
            if (data.clipData != null) {
                for (i in 0..data.clipData.itemCount - 1)
                    addAttachment(data.clipData.getItemAt(i).uri)
            } else {//single file
                addAttachment(data.data)
            }
        }
    }

    @OnTouch(R.id.root, R.id.content)
    fun onElseClick(): Boolean {
        fabs.onEach { it.show() }
        return false
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

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.END)) {
            drawer_layout.closeDrawer(Gravity.END)
            return
        }

        if (hasDayChanged()) {
            AlertUtility.showConfirmationAlert(this, R.string.discard_title, R.string.discard_confirmation,
                    DialogInterface.OnClickListener { _, _ -> super.onBackPressed() })
            return
        }
        super.onBackPressed()
    }

    private fun hasDayChanged() =
            initialHashDay != day.hashCode() || initialHashAttachments != attachmentManager.getAll().toList().hashCode()

    @OnClick(R.id.fabAttachments)
    fun onFabAttachmentsClick() {
        drawer_layout.openDrawer(GravityCompat.END)
    }

    @OnClick(R.id.fab_save)
    fun onFabSaveClick() {
        put()
    }

    @OnClick(R.id.fab_delete)
    fun onFabDeleteClick() {
        AlertUtility.showConfirmationAlert(this, R.string.remove_day_title, R.string.remove_day_confirmation, DialogInterface.OnClickListener { dialogInterface, i ->
            delete()
        })
    }

    @OnClick(R.id.fab_add_attachment)
    fun onFabAddAttachmentClick() {
        FileUtils.selectFileIntent(this, Constants.REQUEST_CODE_SELECT_ATTACHMENT)
    }

    private fun put() {
        diaryPresenter.put(day, attachmentManager.getAll().toList())
    }

    private fun delete() {
        diaryPresenter.delete(day)
    }

    private fun showError(errorMessage: String) {
        Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show()
    }

    private fun addAttachment(uri: Uri) {
        try {
            val fileBytes = FileUtils.readFileFromUri(uri, this)
            val mimeType = FileUtils.readMimeTypeFromUri(uri, this)

            attachmentManager.add(fileBytes, mimeType)
        } catch (e: IOException) {
            e.printStackTrace()
            showError(getString(R.string.attachment_add_error))
        }

    }

    override fun onDestroy() {
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
