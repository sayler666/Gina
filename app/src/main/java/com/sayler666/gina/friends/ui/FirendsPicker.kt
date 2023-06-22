package com.sayler666.gina.friends.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.sayler666.core.file.Files
import com.sayler666.gina.friends.viewmodel.FriendEditViewModel
import com.sayler666.gina.friends.viewmodel.FriendEntity
import com.sayler666.gina.ui.dialog.ConfirmationDialog
import com.sayler666.gina.ui.theme.defaultTextFieldBorder
import com.sayler666.gina.ui.theme.secondaryTextColors


@Composable
fun FriendsPicker(
    showPopup: Boolean,
    friends: List<FriendEntity>,
    searchValue: String,
    onDismiss: () -> Unit,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit,
    onFriendClicked: (Int, Boolean) -> Unit,
    selectable: Boolean = true
) {
    if (showPopup)
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .heightIn(100.dp, 350.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                FriendsList(
                    friends,
                    selectable,
                    onFriendClicked,
                    searchValue,
                    onSearchChanged,
                    onAddNewFriend
                )
            }
        }
}

@Composable
fun FriendsList(
    friends: List<FriendEntity>,
    selectable: Boolean,
    onFriendClicked: (Int, Boolean) -> Unit,
    searchValue: String,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (list, textField) = createRefs()
        LazyColumn(modifier = Modifier
            .clipToBounds()
            .fillMaxWidth()
            .constrainAs(list) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(textField.top)
                height = Dimension.fillToConstraints
            },
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
            content = {
                items(friends) {
                    FriendComponent(friend = it,
                        selectable = selectable,
                        onClick = { selected ->
                            onFriendClicked(it.id, selected)
                        }
                    )
                }
            })
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(textField) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                .padding(8.dp),
            searchValue,
            onSearchChanged,
            onAddNewFriend
        )
    }
}

fun handleAvatar(
    uri: Uri?,
    context: Context,
    applyAvatar: (ByteArray) -> Unit
) {

    fun createAttachment(uri: Uri): ByteArray {
        val (content, _) = Files.readBytesAndMimeTypeFromUri(uri, context)
        return content
    }

    uri?.let {
        applyAvatar(createAttachment(uri))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendEdit(
    showPopup: Boolean,
    friendId: Int,
    onDismiss: () -> Unit,
    viewModel: FriendEditViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val addAvatar = rememberLauncherForActivityResult(PickVisualMedia()) {
        handleAvatar(it, context) { avatar ->
            viewModel.changeAvatar(avatar)
        }
    }
    val request: PickVisualMediaRequest = PickVisualMediaRequest.Builder()
        .setMediaType(ImageOnly)
        .build()

    LaunchedEffect(friendId) {
        viewModel.loadFriend(friendId)
    }

    val friendEntity: FriendEntity? by viewModel.friend.collectAsStateWithLifecycle()
    friendEntity?.let { friend ->
        val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
        if (showPopup) {
            Dialog(onDismissRequest = { onDismiss() }) {
                val name = remember(friend) { mutableStateOf(friend.name) }
                Card(
                    modifier = Modifier
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(top = 8.dp, end = 8.dp, bottom = 8.dp)
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Box {
                            FriendIcon(friend)
                            Icon(
                                Filled.Edit,
                                null,
                                tint = colorScheme.secondary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .combinedClickable(
                                        onClick = { addAvatar.launch(request) },
                                        onLongClick = { viewModel.clearAvatar() }
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedTextField(
                            modifier = Modifier.defaultTextFieldBorder(),
                            value = name.value,
                            onValueChange = {
                                name.value = it
                                viewModel.changeName(it)
                            },
                            colors = secondaryTextColors(),
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp),
                    ) {
                        IconButton(onClick = {
                            showDeleteConfirmationDialog.value = true
                        }) {
                            Icon(Filled.Delete, null, tint = colorScheme.error)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            viewModel.updateFriend()
                            onDismiss()
                        }) {
                            Icon(Filled.Save, null, tint = colorScheme.primary)
                        }

                        ConfirmationDialog(
                            title = "Remove ${friend.name}",
                            text = "Do you really want to remove this friend?",
                            confirmButtonText = "Remove",
                            dismissButtonText = "Cancel",
                            showDialog = showDeleteConfirmationDialog,
                        ) {
                            showDeleteConfirmationDialog.value = false
                            viewModel.deleteFriend()
                            onDismiss()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTextField(
    modifier: Modifier = Modifier,
    searchValue: String,
    onValueChanged: (String) -> Unit,
    onDone: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier.defaultTextFieldBorder(),
        value = searchValue,
        onValueChange = onValueChanged,
        colors = secondaryTextColors(),
        trailingIcon = {
            AnimatedVisibility(
                visible = searchValue.isNotBlank(), enter = fadeIn(), exit = fadeOut()
            ) {
                IconButton(onClick = {
                    onDone(searchValue)
                }) {
                    Icon(
                        imageVector = Filled.AddCircle,
                        contentDescription = null,
                        tint = colorScheme.secondary
                    )
                }
            }
        },
        placeholder = { Text(text = "Search or add new friend...") },
        maxLines = 1,
        singleLine = true
    )
}

@Composable
fun FriendComponent(
    friend: FriendEntity,
    onClick: (Boolean) -> Unit,
    selectable: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(46.dp)
            .clickable {
                onClick(!friend.selected)
            }) {
        Spacer(modifier = Modifier.width(8.dp))

        FriendIcon(friend)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "${friend.name} (${friend.daysCount})",
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1
        )

        Spacer(modifier = Modifier.weight(1f))

        if (selectable) RadioButton(
            selected = friend.selected,
            onClick = { onClick(!friend.selected) }
        )
    }
}

@Composable
fun FriendIcon(friend: FriendEntity, modifier: Modifier = Modifier, size: Dp = 38.dp) {
    Box(
        modifier = modifier
            .shadow(elevation = 3.dp, shape = CircleShape)
            .background(colorScheme.primary, shape = CircleShape)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        if (friend.avatar != null) {
            Image(
                contentScale = ContentScale.Crop,
                painter = rememberAsyncImagePainter(friend.avatar),
                contentDescription = "",
                modifier = Modifier
                    .shadow(elevation = 4.dp, shape = CircleShape)
            )
        } else {
            Text(
                text = friend.initials,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
                    .copy(color = colorScheme.background, fontSize = 17.sp)
            )
        }
    }
}
