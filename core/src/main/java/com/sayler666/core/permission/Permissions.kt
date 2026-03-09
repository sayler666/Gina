package com.sayler666.core.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object Permissions {
    fun getManageAllFilesSettingsIntent(context: Context): Intent {
        val uri = Uri.parse("package:${context.packageName}")
        return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
    }
}
