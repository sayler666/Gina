package com.sayler666.gina.core.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.sayler666.gina.BuildConfig

object Permissions {
    fun getManageAllFilesSettingsIntent(): Intent {
        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
    }
}
