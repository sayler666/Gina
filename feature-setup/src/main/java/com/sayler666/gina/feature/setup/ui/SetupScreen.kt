package com.sayler666.gina.feature.setup.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sayler666.core.compose.effect.CollectFlowWithLifecycleEffect
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewAction
import com.sayler666.gina.feature.setup.viewmodel.SetupViewModel.ViewEvent
import com.sayler666.gina.navigation.routes.Journal
import com.sayler666.gina.resources.R.string.select_database_create_database
import com.sayler666.gina.resources.R.string.select_database_open_database
import com.sayler666.gina.ui.LocalNavigator

@Composable
fun SetupScreen(viewModel: SetupViewModel = hiltViewModel()) {
    val navigator = LocalNavigator.current

    CollectFlowWithLifecycleEffect(viewModel.viewActions) { action ->
        when (action) {
            ViewAction.NavigateToJournal -> navigator.navigateToRoot(Journal)
        }
    }

    Content(viewEvent = viewModel::onViewEvent)
}

@Composable
private fun Content(viewEvent: (ViewEvent) -> Unit) {
    val openDocumentLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        uri?.let { viewEvent(ViewEvent.OnDatabaseSelected(it)) }
    }
    val createDocumentLauncher = rememberLauncherForActivityResult(CreateDocument("application/vnd.sqlite3")) { uri ->
        uri?.let { viewEvent(ViewEvent.OnNewDatabaseCreated(it)) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = { openDocumentLauncher.launch(arrayOf("*/*")) }
        ) {
            Text(
                style = typography.labelLarge,
                text = stringResource(select_database_open_database)
            )
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = { createDocumentLauncher.launch("gina_journal.db") }
        ) {
            Text(
                style = typography.labelLarge,
                text = stringResource(select_database_create_database)
            )
        }
    }
}
