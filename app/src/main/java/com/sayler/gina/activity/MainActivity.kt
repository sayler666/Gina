package com.sayler.gina.activity

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnLongClick
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.sayler.gina.GinaApplication
import com.sayler.gina.R
import com.sayler.gina.adapter.DaysAdapter
import com.sayler.gina.domain.DataManager
import com.sayler.gina.domain.IDay
import com.sayler.gina.domain.presenter.diary.DiaryContract
import com.sayler.gina.domain.presenter.list.ShowListContract
import com.sayler.gina.store.settings.SettingsStore
import com.sayler.gina.store.settings.SettingsStoreManager
import com.sayler.gina.ui.DefaultScrollerViewProvider
import com.sayler.gina.ui.UiStateController
import com.sayler.gina.util.AlertUtility
import com.sayler.gina.util.BroadcastReceiverHelper
import com.sayler.gina.util.Constants
import com.sayler.gina.util.FileUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.a_main.*
import kotlinx.android.synthetic.main.i_error_content.*
import kotlinx.android.synthetic.main.i_main_content.*
import kotlinx.android.synthetic.main.i_progress_bar.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var dataManager: DataManager<*>
    @Inject
    lateinit var settingsStoreManager: SettingsStoreManager
    @Inject
    lateinit var diaryPresenter: DiaryContract.Presenter

    @Inject
    lateinit var showListPresenter: ShowListContract.Presenter
    private lateinit var uiStateController: UiStateController
    private lateinit var broadcastReceiverRefresh: BroadcastReceiverHelper
    private var daysAdapter: DaysAdapter? = null
    private var searchView: SearchView? = null

    private val showListView = object : ShowListContract.View {


        override fun show(dayList: List<IDay>) {
            updateRecyclerView(dayList)
            uiStateController.setUiStateContent()
        }

        override fun statistics(statistics: String) {
            if (statistics.isNotEmpty())
                AlertUtility.showInfoAlert(this@MainActivity, R.string.menu_statistics, statistics)
        }

        override fun showProgress() {
            uiStateController.setUiStateLoading()
        }

        override fun hideProgress() {
            //do nothing
        }

        override fun noDataSource() {
            uiStateController.setUiStateEmpty()
        }

        override fun timeout() {
            uiStateController.setUiStateError()
            errorText.text = "Timoeut error"
        }

        override fun syntaxError() {
            uiStateController.setUiStateError()
            errorText.text = "Syntax error"
        }

        override fun error() {
            uiStateController.setUiStateError()
            errorText.text = " Error"
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)
        ButterKnife.bind(this)

        GinaApplication.dataComponentForActivity(this).inject(this)

        bindPresenters()

        setupBroadcastReceivers()

        setupViews()

        askFormPermission()

        openRememberedSourceFile()
    }

    private fun bindPresenters() {
        bindPresenter(showListPresenter, showListView)
    }

    private fun load() {
        showListPresenter.loadAll()
    }

    private fun setupViews() {
        setupToolbar()

        setupUiStateController()

        setupRecyclerView()
    }

    private fun setupBroadcastReceivers() {
        broadcastReceiverRefresh = BroadcastReceiverHelper { load() }
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.BROADCAST_EDIT_DAY)
        intentFilter.addAction(Constants.BROADCAST_DELETE_DAY)
        broadcastReceiverRefresh.register(this, intentFilter)
    }

    private fun setupUiStateController() {
        uiStateController = UiStateController.Builder()
                .withContentUi(content)
                .withLoadingUi(progressBar)
                .withErrorUi(error)
                .withEmptyUi(noDataSource)
                .build()

        uiStateController.setUiStateEmpty()
    }

    override fun onResume() {
        super.onResume()
        broadcastReceiverRefresh.callScheduledAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data?.path != null)
            setNewDbFilePath(data.data.path)
    }

    /**
     * -----------------------------------------REMEMBERED FILE BEGINNING-----------------------------------------------
     */

    private fun openRememberedSourceFile() {
        if (isSourceFileRemembered) {
            setNewDbFilePath(rememberedSourceFile)
        }
    }

    private fun toggleRememberSourceFile(): Boolean {
        //toggle saved file
        var settingsStore = settingsStoreManager.get()
        return if (settingsStore == null) {
            //save current opened file if empty settings store empty
            settingsStore = SettingsStore(dataManager.getSourceFilePath())
            settingsStoreManager.save(settingsStore)
            true
        } else {
            //clear current opened file if settings store not empty
            settingsStoreManager.clear()
            false
        }
    }

    private fun openSourceFileSelectIntent() {
        FileUtils.selectFileIntent(this, Constants.REQUEST_CODE_SELECT_DB)
    }

    private fun setNewDbFilePath(newSourceFile: String) {
        if (newSourceFile.isNotEmpty()) {
            dataManager.setSourceFile(newSourceFile)
            load()
        }
    }

    private val isSourceFileRemembered: Boolean
        get() {
            val settingsStore = settingsStoreManager.get()
            return settingsStore?.dataSourceFilePath != null
        }

    private val rememberedSourceFile: String
        get() {
            val settingsStore = settingsStoreManager.get()
            return settingsStore?.dataSourceFilePath ?: ""
        }

    /**
     * -----------------------------------------REMEMBERED FILE END-----------------------------------------------------
     */

    /**
     * -----------------------------------------LIST BEGINNING----------------------------------------------------------
     */

    private fun setupRecyclerView() {
        //recycler view
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        daysAdapter = DaysAdapter(this, emptyList())
        recyclerView.adapter = daysAdapter

        //sticky header
        val stickyRecyclerHeadersDecoration = StickyRecyclerHeadersDecoration(daysAdapter)
        recyclerView.addItemDecoration(stickyRecyclerHeadersDecoration)
        recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.divider).marginResId(R.dimen.p_medium).build())
        daysAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                stickyRecyclerHeadersDecoration.invalidateHeaders()
            }
        })

        //fast scroll
        fastscroll.setRecyclerView(recyclerView!!)
        val defaultScrollerViewProvider = DefaultScrollerViewProvider()
        defaultScrollerViewProvider.onHandleVisibilityChangeListener = { visible ->
            changeFabsVisibility(!visible)
        }
        fastscroll.setViewProvider(defaultScrollerViewProvider)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                changeFabsVisibility(newState == RecyclerView.SCROLL_STATE_IDLE)
            }
        })

        //on viewModel click
        daysAdapter?.setOnClick { item, view, _ ->
            val intent = DayActivity.newIntentShowDay(this, item.id)
            //shared elements
            val dayText: View? = view.findViewById(R.id.day)
            val pair1 = Pair.create(dayText, dayText?.transitionName)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1)
            this.startActivity(intent, options.toBundle())

        }
    }

    private fun updateRecyclerView(items: List<IDay>) {
        daysAdapter?.items = items
        daysAdapter?.notifyDataSetChanged()
    }

    private fun changeFabsVisibility(visible: Boolean) {
        if (visible) {
            fabAddNewDay.show()
        } else {
            fabAddNewDay.hide()
        }
    }

    /**
     * -----------------------------------------LIST END----------------------------------------------------------------
     */

    /**
     * -----------------------------------------OPTIONS MENU BEGINNING--------------------------------------------------
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)

        setupSearchView(menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                showPageTitle()
                clearSearchViewAndHide()
                return true
            }
            R.id.file -> {
                openSourceFileSelectIntent()
                return true
            }
            R.id.statistics -> {
                showListPresenter.calculateStatistics()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * -----------------------------------------OPTIONS MENU END--------------------------------------------------------
     */

    /**
     * -----------------------------------------TOOLBAR BEGINNING-------------------------------------------------------
     */

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        setupTitle()
    }

    private fun setupTitle() {
        if (isSourceFileRemembered) {
            pageTitle.setTextColor(Color.WHITE)
        } else {
            pageTitle.setTextColor(Color.BLACK)
        }
        pageTitle.setText(R.string.app_name)
    }

    private fun showPageTitle() {
        pageTitle.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onBackPressed() {
        if (!searchView!!.isIconified) {
            showPageTitle()
            clearSearchViewAndHide()
        } else {
            finish()
            super.onBackPressed()
        }
    }

    private fun setupSearchView(menu: Menu) {
        val menuItem = menu.findItem(R.id.action_search)

        searchView = menuItem.actionView as SearchView

        searchView?.maxWidth = Integer.MAX_VALUE
        val v: View? = searchView?.findViewById(android.support.v7.appcompat.R.id.search_plate)
        v?.setBackgroundColor(Color.TRANSPARENT)

        searchView?.setOnCloseListener {
            showPageTitle()
            load()
            false
        }

        searchView?.setOnSearchClickListener { _ ->
            pageTitle.visibility = View.GONE
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        RxSearchView.queryTextChanges(searchView!!)
                .debounce(1, TimeUnit.SECONDS)
                .filter { charSequence -> charSequence.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { _ -> uiStateController.setUiStateLoading() }
                .subscribe({ this.searchForText(it) })
    }

    private fun searchForText(charSequence: CharSequence) {
        showListPresenter.loadByTextSearch(charSequence.toString())
    }

    private fun clearSearchViewAndHide() {
        searchView?.setQuery("", false)
        searchView?.isIconified = true
    }

    /**
     * -----------------------------------------TOOLBAR END-------------------------------------------------------------
     */


    /**
     * ----------------------------------------PERMISSIONS BEGINNING----------------------------------------------------
     */

    private fun askFormPermission() {
        RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        openRememberedSourceFile()
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.permission_rejected) + Manifest.permission.WRITE_EXTERNAL_STORAGE, Snackbar.LENGTH_SHORT).show()
                    }
                }
    }

    /**
     * ----------------------------------------PERMISSIONS END----------------------------------------------------------
     */

    @OnClick(R.id.fabAddNewDay)
    fun onFabAddDayClick() {
        startActivity(DayEditActivity.newIntentNewDay(this))
    }

    @OnLongClick(R.id.pageTitle)
    fun onToolbarTitleLongPress(): Boolean {
        //check if any file opened
        return if (dataManager.isOpen) {
            toggleRememberSourceFile()
            setupTitle()
            true
        } else false
    }

    @OnClick(R.id.selectDataSourceButton)
    fun onSelectDataSourceButton() {
        openSourceFileSelectIntent()
    }

    override fun onDestroy() {
        dataManager.close()
        super.onDestroy()
    }

}
