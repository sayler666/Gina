package com.sayler666.gina.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FilterListOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sayler666.core.collections.mutate
import com.sayler666.gina.ui.theme.defaultTextFieldBorder
import com.sayler666.gina.ui.theme.secondaryTextColors
import kotlinx.coroutines.launch
import mood.Mood
import mood.ui.mapToMoodIcon


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun FiltersBar(
    title: String,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    moodFilters: List<Mood> = emptyList(),
    onMoodFiltersUpdate: (List<Mood>) -> Unit = {},
    onResetFiltersClicked: () -> Unit,
    filtersActive: Boolean,
    onSearchVisibilityChanged: (Boolean) -> Unit
) {
    val showSearch = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = showSearch, block = {
        snapshotFlow { showSearch.value }.collect { isVisible ->
            onSearchVisibilityChanged(isVisible)
        }
    })

    TopAppBar(title = { if (showSearch.value.not()) Text(title) }, navigationIcon = {}, actions = {
        SearchField(showSearch, searchText, onSearchTextChanged, onClearClick)

        Filters(
            filtersActive,
            moodFilters,
            onResetFiltersClicked = onResetFiltersClicked,
            onMoodsSelected = onMoodFiltersUpdate
        )
    })

}

@ExperimentalComposeUiApi
@Composable
private fun SearchField(
    showSearch: MutableState<Boolean>,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit
) {
    var showClearButton by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardDismissed = rememberSaveable { mutableStateOf(false) }

    if (showSearch.value.not()) IconButton(onClick = { showSearch.value = true }) {
        Icon(
            imageVector = Filled.Search,
            contentDescription = null
        )
    }

    if (showSearch.value) OutlinedTextField(modifier = Modifier
        .wrapContentWidth()
        .padding(start = 0.dp, end = 1.dp)
        .defaultTextFieldBorder()
        .onFocusChanged { focusState ->
            showClearButton = focusState.isFocused
        }
        .focusRequester(focusRequester),
        value = searchText,
        onValueChange = onSearchTextChanged,
        colors = secondaryTextColors(),
        trailingIcon = {
            AnimatedVisibility(
                visible = showSearch.value, enter = fadeIn(), exit = fadeOut()
            ) {
                IconButton(onClick = {
                    onClearClick()
                    showSearch.value = false
                    keyboardDismissed.value = false
                }) {
                    Icon(
                        imageVector = Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        placeholder = { Text("Search...") },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusManager.clearFocus()
            keyboardDismissed.value = true
        })
    )

    LaunchedEffect(showSearch.value) {
        if (showSearch.value && keyboardDismissed.value.not()) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Filters(
    filtersActive: Boolean,
    moodFilters: List<Mood>,
    onMoodsSelected: (List<Mood>) -> Unit,
    onResetFiltersClicked: () -> Unit
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    IconButton(onClick = {
        scope.launch { openBottomSheet = !openBottomSheet }
    }) {
        Box {
            Icon(
                imageVector = Filled.FilterList,
                contentDescription = null
            )
            if (filtersActive) Box(
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
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets(bottom = 0.dp),
                    navigationIcon = {
                        IconButton(onClick = {
                            onResetFiltersClicked()
                        }) {
                            Icon(Rounded.FilterListOff, contentDescription = "Reset filters")
                        }
                    }, title = {
                        Text("Filters")
                    }, actions = {
                        IconButton(onClick = {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) openBottomSheet = false
                            }
                        }) {
                            Icon(Rounded.Check, contentDescription = "Save")
                        }
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )
                MoodFilter(moodFilters, onMoodsSelected)
            }
        }
    }
}

@Composable
fun MoodFilter(
    moodFilters: List<Mood>,
    onSelectMood: (List<Mood>) -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Mood.values().forEach { mood ->
            var checked = moodFilters.any { it == mood }
            IconToggleButton(
                checked = checked,
                onCheckedChange = {
                    checked = !checked
                    val newMoods =
                        moodFilters.mutate { if (checked) it.add(mood) else it.remove(mood) }
                    onSelectMood(newMoods)
                },
                colors = IconButtonDefaults.iconToggleButtonColors(),
            ) {
                val moodIcon = mood.mapToMoodIcon()
                Icon(
                    painter = rememberVectorPainter(image = moodIcon.icon),
                    tint = if (checked) moodIcon.color else MaterialTheme.colorScheme.outline,
                    contentDescription = null,
                )
            }
        }
    }
}
