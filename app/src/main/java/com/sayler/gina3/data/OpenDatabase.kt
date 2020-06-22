package com.sayler.gina3.data

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class OpenDatabase : ActivityResultContract<String, String?>() {
    override fun createIntent(context: Context, type: String) =
        Intent(Intent.ACTION_GET_CONTENT)
            .setType(type)

    override fun parseResult(resultCode: Int, intent: Intent?): String? = intent?.data?.path

    companion object{
        const val ANY_FILE = "application/sqlite"
    }
}