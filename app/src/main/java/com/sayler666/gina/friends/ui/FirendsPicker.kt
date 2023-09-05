package com.sayler666.gina.friends.ui

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sayler666.gina.friends.viewmodel.FriendEntity


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
                    friends = friends,
                    selectable = selectable,
                    onFriendClicked = onFriendClicked,
                    searchValue = searchValue,
                    onSearchChanged = onSearchChanged,
                    onAddNewFriend = onAddNewFriend
                )
            }
        }
}
