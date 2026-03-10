package com.sayler666.gina.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sayler666.core.collections.mutate
import com.sayler666.core.compose.conditional
import com.sayler666.domain.model.journal.Mood
import com.sayler666.gina.mood.ui.mapToMoodIcon
import com.sayler666.gina.ui.theme.defaultTextFieldBorder
import com.sayler666.gina.ui.theme.defaultTextFieldColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun FiltersBar(
    modifier: Modifier = Modifier,
    title: String,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    moodFilters: List<Mood> = emptyList(),
    onMoodFiltersUpdate: (List<Mood>) -> Unit = {},
    onResetFiltersClicked: () -> Unit,
    filtersActive: Boolean,
    onSearchVisibilityChanged: (Boolean) -> Unit,
    hazeState: HazeState? = null,
) {
    val searchVisible = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = searchVisible, block = {
        snapshotFlow { searchVisible.value }.collect { isVisible ->
            onSearchVisibilityChanged(isVisible)
        }
    })

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Content(
                searchVisible = searchVisible.value,
                title = title,
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                onClearClick = onClearClick,
                onHideSearchClick = { searchVisible.value = false },
                onShowSearchClick = { searchVisible.value = true },
                hazeState = hazeState
            )
        },
        actions = {
            Filters(
                filtersActive = filtersActive,
                moodFilters = moodFilters,
                onResetFiltersClicked = onResetFiltersClicked,
                onMoodsSelected = onMoodFiltersUpdate
            )
        }
    )
}

@ExperimentalComposeUiApi

@Composable
private fun Content(
    searchVisible: Boolean,
    title: String,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onHideSearchClick: () -> Unit,
    onShowSearchClick: () -> Unit,
    hazeState: HazeState?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AnimatedContent(
            targetState = searchVisible,
            transitionSpec = {
                slideInVertically(tween(300)) + fadeIn(tween(200)) togetherWith fadeOut(tween(200))
            },
        ) { isSearchVisible ->
            if (!isSearchVisible) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = title, modifier = Modifier.padding(top = 10.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { onShowSearchClick() }) {
                        Icon(
                            imageVector = Filled.Search,
                            contentDescription = null
                        )
                    }
                }
            } else {
                SearchField(
                    visible = searchVisible,
                    searchText = searchText,
                    onSearchTextChanged = onSearchTextChanged,
                    onClearClick = onClearClick,
                    onHideSearchClick = onHideSearchClick,
                    hazeState = hazeState
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun SearchField(
    visible: Boolean,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onHideSearchClick: () -> Unit,
    hazeState: HazeState? = null,
) {
    val showClearButton by remember { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardDismissed = rememberSaveable { mutableStateOf(false) }
    val hazeTint = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .defaultTextFieldBorder()
            .conditional(hazeState != null) {
                hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        blurRadius = 16.dp,
                        tint = HazeTint(hazeTint)
                    )
                )
            }
            .focusRequester(focusRequester),
        value = searchText,
        onValueChange = onSearchTextChanged,
        colors = defaultTextFieldColors(),
        textStyle = MaterialTheme.typography.labelLarge,
        trailingIcon = {
            AnimatedVisibility(
                visible = showClearButton,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = {
                    onClearClick()
                    onHideSearchClick()
                    keyboardDismissed.value = false
                }) {
                    Icon(
                        imageVector = Filled.Close,
                        contentDescription = null,
                    )
                }
            }
        },
        placeholder = {
            Text(
                text = "Search...",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
            )
        },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusManager.clearFocus()
            keyboardDismissed.value = true
        })
    )

    LaunchedEffect(visible) {
        if (visible && keyboardDismissed.value.not()) {
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
            Column {
                CenterAlignedTopAppBar(
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
        Mood.entries.forEach { mood ->
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
