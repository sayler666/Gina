package com.sayler.monia.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sayler.monia.R
import com.sayler.monia.attachment.AttachmentAdapter
import com.sayler.monia.domain.IAttachment
import com.sayler.monia.util.FileUtils
import com.sayler.monia.util.ViewSliderCoordinator
import kotlinx.android.synthetic.main.f_attachment.*

/**
 * Created by sayler on 2017-06-23.
 *

 */
class AttachmentFragment : Fragment(), ViewSliderCoordinator.Slideable {


    lateinit var attachments: ArrayList<IAttachment>

    companion object {
        val ATTACHMENTS_EXTRA = "ATTACHMENTS_EXTRA"
        fun newInstance(attachments: ArrayList<IAttachment>): AttachmentFragment {
            val fragment = AttachmentFragment()
            val bundle = Bundle()

            bundle.putParcelableArrayList(ATTACHMENTS_EXTRA, attachments)

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.f_attachment, container, false)

        readExtras()

        return rootView
    }

    fun readExtras() {
        if (arguments.containsKey(ATTACHMENTS_EXTRA)) {
            attachments = arguments.getParcelableArrayList<IAttachment>(ATTACHMENTS_EXTRA)
        } else {
            throw IllegalStateException("Missing extra params: ATTACHMENTS_EXTRA")
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    fun setupViews() {
        countText.text = attachments.size.toString()

        //drawer
        val layoutManager = LinearLayoutManager(activity)
        attachmentsRecyclerView.layoutManager = layoutManager
        val attachmentAdapter = AttachmentAdapter(attachments, attachmentsRecyclerView)
        attachmentAdapter.setOnClick({ item, _ ->
            with(item.attachment) {
                FileUtils.openFileIntent(this@AttachmentFragment.activity, file, mimeType, this@AttachmentFragment.activity.applicationContext.packageName + ".provider")
            }
        })
        attachmentsRecyclerView.adapter = attachmentAdapter
    }

    override fun onSlide(progress: Float) {
        minimizedContainer.alpha = progress
        maximizedContainer.alpha = 1.0f - progress

        if (progress < 1) {
            maximizedContainer.visibility = View.VISIBLE
        } else if (progress == 1.0f) {
            maximizedContainer.visibility = View.GONE
        }
    }

}