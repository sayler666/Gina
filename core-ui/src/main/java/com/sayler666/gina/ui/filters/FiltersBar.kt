package com.sayler666.gina.ui.filters

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sayler666.core.compose.conditional
import com.sayler666.gina.ui.theme.defaultTextFieldBorder
import com.sayler666.gina.ui.theme.defaultTextFieldColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun FiltersBar(
    modifier: Modifier = Modifier,
    title: String,
    filtersState: FiltersState,
    onFiltersChanged: (FiltersState) -> Unit,
    hazeState: HazeState? = null,
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            TitleArea(
                searchVisible = filtersState.searchVisible,
                title = title,
                searchText = filtersState.searchQuery,
                onSearchTextChanged = { onFiltersChanged(filtersState.copy(searchQuery = it)) },
                onClearClick = { onFiltersChanged(filtersState.copy(searchQuery = "", searchVisible = false)) },
                onShowSearchClick = { onFiltersChanged(filtersState.copy(searchVisible = true)) },
                hazeState = hazeState
            )
        },
        actions = {
            FiltersButton(
                filtersState = filtersState,
                onFiltersChanged = onFiltersChanged
            )
        }
    )
}

@ExperimentalComposeUiApi
@Composable
private fun TitleArea(
    searchVisible: Boolean,
    title: String,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onShowSearchClick: () -> Unit,
    hazeState: HazeState?
) {
    Row(modifier = Modifier.fillMaxWidth()) {
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
                    IconButton(onClick = onShowSearchClick) {
                        Icon(imageVector = Filled.Search, contentDescription = null)
                    }
                }
            } else {
                SearchField(
                    visible = searchVisible,
                    searchText = searchText,
                    onSearchTextChanged = onSearchTextChanged,
                    onClearClick = onClearClick,
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
    hazeState: HazeState? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardDismissed = rememberSaveable { mutableStateOf(false) }
    val hazeTint = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
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
            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = {
                    onClearClick()
                    keyboardDismissed.value = false
                }) {
                    Icon(imageVector = Filled.Close, contentDescription = null)
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
