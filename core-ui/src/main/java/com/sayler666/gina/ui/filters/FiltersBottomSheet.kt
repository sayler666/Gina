package com.sayler666.gina.ui.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FilterListOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sayler666.gina.resources.R
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun FiltersButton(
    filtersState: FiltersState,
    onFiltersChanged: (FiltersState) -> Unit,
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    IconButton(onClick = { scope.launch { openBottomSheet = !openBottomSheet } }) {
        Box {
            Icon(
                imageVector = Filled.FilterList,
                contentDescription = null
            )
            if (filtersState.filtersActive) Box(
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.TopEnd)
                    .absoluteOffset(y = 2.dp)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            )
        }
    }

    if (openBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            sheetState = sheetState,
            onDismissRequest = { scope.launch { openBottomSheet = false } }
        ) {
            Column {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { onFiltersChanged(FiltersState()) }) {
                            Icon(
                                Rounded.FilterListOff,
                                contentDescription = stringResource(R.string.filters_reset_content_description)
                            )
                        }
                    },
                    title = { Text(stringResource(R.string.filters_title)) },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) openBottomSheet = false
                            }
                        }) {
                            Icon(
                                Rounded.Check,
                                contentDescription = stringResource(R.string.filters_save_content_description)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )
                FilterSection(stringResource(R.string.filters_section_mood)) {
                    MoodFilter(filtersState.moods) { newMoods ->
                        onFiltersChanged(filtersState.copy(moods = newMoods))
                    }
                }
                FilterSection(stringResource(R.string.filters_section_date_range)) {
                    DateRangeFilter(filtersState.dateRange) { newRange ->
                        onFiltersChanged(filtersState.copy(dateRange = newRange))
                    }
                }
                // Future: FilterSection("Friends") { FriendsFilter(...) }
            }
        }
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content()
    }
}
