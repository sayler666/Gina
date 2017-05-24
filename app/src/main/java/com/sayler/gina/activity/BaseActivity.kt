/**
 * Created by sayler on 2016-11-22.
 *
 *
 * Copyright 2016 MiQUiDO <http:></http:>//www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
