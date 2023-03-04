package com.sayler666.gina.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sayler666.gina.ui.theme.secondaryColors


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SearchBar(
    title: String,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {}
) {
    var showClearButton by rememberSaveable { mutableStateOf(false) }
    val showSearch = rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardDismissed = rememberSaveable { mutableStateOf(false) }

    TopAppBar(title = { Text(title) }, navigationIcon = {}, actions = {
        if (showSearch.value.not()) IconButton(onClick = { showSearch.value = true }) {
            Icon(
                imageVector = Icons.Filled.Search,
                modifier = Modifier,
                contentDescription = null
            )
        }
        if (showSearch.value) OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 1.dp)
            .border(
                BorderStroke(
                    1.dp, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                ), shape = MaterialTheme.shapes.large
            )
            .clip(RoundedCornerShape(4.dp))
            .onFocusChanged { focusState ->
                showClearButton = focusState.isFocused
            }
            .focusRequester(focusRequester),
            value = searchText,
            onValueChange = onSearchTextChanged,
            colors = secondaryColors(),
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
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
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
    })

    LaunchedEffect(showSearch.value) {
        if (showSearch.value && keyboardDismissed.value.not()) {
            focusRequester.requestFocus()
        }
    }
}
