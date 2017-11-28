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
import com.sayler.gina.permission.PermissionUtils
import com.sayler.gina.stats.StatisticPair
import com.sayler.gina.stats.decorator.CharsDecorator
import com.sayler.gina.stats.decorator.EntriresStatistic
import com.sayler.gina.stats.decorator.SentencesDecorator
import com.sayler.gina.stats.decorator.WordsDecorator
import com.sayler.gina.store.settings.SettingsStore
import com.sayler.gina.store.settings.SettingsStoreManager
import com.sayler.gina.ui.UiStateController
import com.sayler.gina.util.AlertUtility
import com.sayler.gina.util.BroadcastReceiverHelper
import com.sayler.gina.util.Constants
import com.sayler.gina.util.FileUtils
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.a_main.*
import kotlinx.android.synthetic.main.i_error_content.*
import kotlinx.android.synthetic.main.i_main_content.*
import kotlinx.android.synthetic.main.i_progress_bar.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseActivity(), PermissionUtils.PermissionCallback {

    @Inject
    lateinit var dataManager: DataManager<*>

    @Inject
    lateinit var diaryPresenter: DiaryContract.Presenter

    @Inject
    lateinit var settingsStoreManager: SettingsStoreManager

    private lateinit var uiStateController: UiStateController
    private lateinit var broadcastReceiverRefresh: BroadcastReceiverHelper
    private var daysAdapter: DaysAdapter? = null
    private var searchView: SearchView? = null
    private var currentSourceFile: String = ""
    private var statistics: String = ""

    private val diaryContractView = object : DiaryContract.View {
        override fun onDownloaded(data: List<IDay>) {
            createRecyclerView(data)
            uiStateController.setUiStateContent()
            setupStatistic(data)
        }

        override fun onNoDataSource() {
            uiStateController.setUiStateEmpty()
        }

        override fun onError(s: String) {
            uiStateController.setUiStateError()
            errorText.text = s
        }

        override fun onDelete() {
            //not used
        }

        override fun onPut() {
            //not used
        }
    }

    private fun setupStatistic(data: List<IDay>) {
        if (data.isNotEmpty()) {
            val statisticGenerator = CharsDecorator(WordsDecorator(SentencesDecorator(EntriresStatistic())))
            var statisticData = ""
            statisticGenerator.generate(data).forEach { statisticPair: StatisticPair ->
                statisticData += "${statisticPair.label}: ${statisticPair.value}\n"
            }
            this.statistics = statisticData.trimEnd('\n')
        } else {
            this.statistics = ""
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

    private fun openRememberedSourceFile() {
        if (isSourceFileRemembered) {
            setNewDbFilePath(rememberedSourceFile)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)

        setupSearchView(menu)

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupSearchView(menu: Menu) {
        val menuItem = menu.findItem(R.id.action_search)

        searchView = menuItem.actionView as SearchView

        searchView?.maxWidth = Integer.MAX_VALUE
        val v = searchView?.findViewById(android.support.v7.appcompat.R.id.search_plate)
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


    private fun bindPresenters() {
        bindPresenter(diaryPresenter, diaryContractView)
    }

    private fun load() {
        uiStateController.setUiStateLoading()
        diaryPresenter.loadAll()
    }

    private fun showPageTitle() {
        pageTitle.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
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
                showStatistic()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showStatistic() {
        if (statistics.isNotEmpty())
            AlertUtility.showInfoAlert(this, R.string.menu_statistics, this.statistics)
    }

    private fun clearSearchViewAndHide() {
        searchView?.setQuery("", false)
        searchView?.isIconified = true
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

    private fun setupBroadcastReceivers() {
        broadcastReceiverRefresh = BroadcastReceiverHelper { load() }
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.BROADCAST_EDIT_DAY)
        intentFilter.addAction(Constants.BROADCAST_DELETE_DAY)
        broadcastReceiverRefresh.register(this, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        broadcastReceiverRefresh.callScheduledAction()
    }

    private fun askFormPermission() {
        if (!PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtils.askForPermission(this, this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun setupViews() {
        setupToolbar()

        setupUiStateController()

        setupRecyclerView()
    }

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

    private fun setupUiStateController() {
        uiStateController = UiStateController.Builder()
                .withContentUi(content)
                .withLoadingUi(progressBar)
                .withErrorUi(error)
                .withEmptyUi(noDataSource)
                .build()

        uiStateController.setUiStateEmpty()
    }

    private val isSourceFileRemembered: Boolean
        get() {
            val settingsStore = settingsStoreManager.get()
            return settingsStore?.dataSourceFilePath != null
        }

    private val rememberedSourceFile: String
        get() {
            val settingsStore = settingsStoreManager.get()
            if (settingsStore != null) {
                return settingsStore.dataSourceFilePath
            } else {
                return ""
            }
        }

    private fun setupRecyclerView() {
        //recycler view
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        daysAdapter = DaysAdapter(this, emptyList<IDay>())
        recyclerView.adapter = daysAdapter

        //sticky header
        val decor = StickyRecyclerHeadersDecoration(daysAdapter)
        recyclerView.addItemDecoration(decor)
        recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.divider).marginResId(R.dimen.p_medium).build())
        fastscroll.setRecyclerView(recyclerView!!)

        daysAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                decor.invalidateHeaders()
            }
        })

        //on viewModel click
        daysAdapter?.setOnClick { item, view, _ ->
            val intent = DayActivity.newIntentShowDay(this, item.id)
            //shared elements
            val dayText = view.findViewById(R.id.day)
            val pair1 = Pair.create(dayText, dayText.transitionName)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1)
            this.startActivity(intent, options.toBundle())

        }
    }

    @OnClick(R.id.fab)
    fun onFabAddDayClick() {
        startActivity(DayEditActivity.newIntentNewDay(this))
    }

    @OnLongClick(R.id.pageTitle)
    fun onToolbarTitleLongPress(): Boolean {
        //check if any file opened
        if (currentSourceFile.isNotEmpty()) {
            toggleRememberSourceFile()
            setupTitle()
            return true
        }
        return false
    }

    private fun toggleRememberSourceFile(): Boolean {
        //toggle saved file
        var settingsStore = settingsStoreManager.get()
        if (settingsStore == null) {
            //save current opened file if empty settings store empty
            settingsStore = SettingsStore(currentSourceFile)
            settingsStoreManager.save(settingsStore)
            return true
        } else {
            //clear current opened file if settings store not empty
            settingsStoreManager.clear()
            return false
        }
    }

    private fun searchForText(charSequence: CharSequence) {
        diaryPresenter.loadByTextSearch(charSequence.toString())
    }

    private fun createRecyclerView(items: List<IDay>) {
        daysAdapter?.items = items
        daysAdapter?.notifyDataSetChanged()
    }

    @OnClick(R.id.selectDataSourceButton)
    fun onSelectDataSourceButton() {
        openSourceFileSelectIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data?.path != null)
            setNewDbFilePath(data.data.path)
    }

    private fun openSourceFileSelectIntent() {
        FileUtils.selectFileIntent(this, Constants.REQUEST_CODE_SELECT_DB)
    }

    private fun setNewDbFilePath(newSourceFile: String) {
        if (newSourceFile.isNotEmpty()) {
            currentSourceFile = newSourceFile
            dataManager.setSourceFile(newSourceFile)
            load()
        }
    }

    override fun onDestroy() {
        dataManager.close()
        super.onDestroy()
    }

    override fun onPermissionGranted(permission: String) {
        load()
    }

    override fun onPermissionRejected(permission: String) {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.permission_rejected) + permission, Snackbar.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
