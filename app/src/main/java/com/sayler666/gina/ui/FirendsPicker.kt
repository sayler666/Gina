package com.sayler666.gina.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.sayler666.gina.dayDetails.viewmodel.FriendEntity
import com.sayler666.gina.ui.theme.RobotoSlabRegular

@Composable
fun FriendsPicker(
    showPopup: Boolean,
    friends: List<FriendEntity>,
    searchValue: String,
    onDismiss: () -> Unit,
    onSearchChanged: (String) -> Unit,
    onAddNewFriend: (String) -> Unit,
    onFriendClicked: (Int, Boolean) -> Unit
) {
    if (showPopup)
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier.padding(32.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(8.dp)) {
                    LazyColumn(modifier = Modifier.heightIn(50.dp, 250.dp), content = {
                        items(friends) {
                            FriendComponent(it, onClick = { selected ->
                                onFriendClicked(it.id, selected)
                            })
                        }
                    })
                    SearchTextField(searchValue, onSearchChanged, onAddNewFriend)
                }
            }
        }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchTextField(
    searchValue: String,
    onValueChanged: (String) -> Unit,
    onDone: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 1.dp)
            .border(
                BorderStroke(
                    1.dp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                ), shape = MaterialTheme.shapes.large
            )
            .clip(RoundedCornerShape(4.dp)),
        value = searchValue,
        onValueChange = onValueChanged,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = 0.2f
            ),
            cursorColor = MaterialTheme.colorScheme.secondary,
            textColor = MaterialTheme.colorScheme.secondary
        ),
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
fun FriendComponent(friend: FriendEntity, onClick: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 6.dp, bottom = 6.dp)
            .clickable {
                onClick(!friend.selected)
            }) {
        val shape = CircleShape
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.primary, shape = shape)
                .shadow(6.dp, shape = shape)
                .size(38.dp)
                .clip(shape = shape),
            contentAlignment = Alignment.Center
        ) {
            if (friend.avatar != null) {
                Image(
                    contentScale = ContentScale.Crop,
                    painter = rememberAsyncImagePainter(friend.avatar),
                    contentDescription = "",
                )
            } else {
                Text(
                    text = friend.initials,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                        .copy(color = MaterialTheme.colorScheme.background)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = friend.name, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
        Spacer(modifier = Modifier.weight(1f))
        RadioButton(
            selected = friend.selected,
            onClick = { onClick(!friend.selected) }
        )
    }
}
