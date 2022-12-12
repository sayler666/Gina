package com.sayler666.gina.ginaApp.viewModel

import androidx.lifecycle.ViewModel
import com.sayler666.gina.db.DatabaseProvider
import com.sayler666.gina.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GinaMainViewModel @Inject constructor(
    settings: Settings,
    databaseProvider: DatabaseProvider
) : ViewModel() {
    var hasRememberedDatabase = settings.getDatabasePath() != null

    init {
        if (hasRememberedDatabase) databaseProvider.openSavedDB()
    }
}
