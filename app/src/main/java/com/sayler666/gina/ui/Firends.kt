package com.sayler666.gina.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.ui.theme.secondaryColors

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
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val (list, textField) = createRefs()
                    LazyColumn(modifier = Modifier
                        .heightIn(50.dp, 250.dp)
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
        }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchTextField(
    modifier: Modifier = Modifier,
    searchValue: String,
    onValueChanged: (String) -> Unit,
    onDone: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .border(
                BorderStroke(
                    1.dp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                ), shape = MaterialTheme.shapes.large
            )
            .clip(RoundedCornerShape(4.dp)),
        value = searchValue,
        onValueChange = onValueChanged,
        colors = secondaryColors(),
        placeholder = { Text(text = "Search or add new friend...") },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onDone(searchValue)
        })
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

        Text(text = friend.name, style = MaterialTheme.typography.bodyLarge, maxLines = 1)

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
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
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
                    .copy(color = MaterialTheme.colorScheme.background, fontSize = 15.sp)
            )
        }
    }
}
