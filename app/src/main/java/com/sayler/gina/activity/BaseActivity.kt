/**
 * Created by sayler on 2016-11-22.
 *
 */
package com.sayler.gina.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.sayler.gina.domain.presenter.BasePresenter
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*

open class BaseActivity : AppCompatActivity() {

    protected var presentersCollection: MutableList<BasePresenter<*>> = ArrayList()

    fun <T> bindPresenter(presenter: BasePresenter<T>, presenterView: T) {
        presenter.bindView(presenterView)
        presentersCollection.add(presenter)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onDestroy() {
        presentersCollection.forEach { presenter: BasePresenter<*> -> presenter.unbindView() }
        presentersCollection.clear()
        super.onDestroy()
    }
}
