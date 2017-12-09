package com.sayler.gina.util

import android.app.Activity
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import com.sayler.gina.R

/**
 * Created by sayler on 2017-09-09.
 *

 */

class AlertUtility {
    companion object {
        fun showConfirmationAlert(activity: Activity, titleRes: Int, messageRes: Int, onClickListener: DialogInterface.OnClickListener) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(titleRes)
            builder.setMessage(messageRes)
            builder.setPositiveButton(R.string.yes, onClickListener)
            builder.setNegativeButton(R.string.no, null)
            builder.show()
        }

        fun showInfoAlert(activity: Activity, titleRes: Int, messageRes: String) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle(titleRes)
                    builder.setMessage(messageRes)
                    builder.setPositiveButton(R.string.ok,null)
                    builder.show()
                }
    }
}