package com.sayler666.gina.friends.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.friends.ui.ManageFriendsScreen
import com.sayler666.gina.navigation.routes.ManageFriends
import com.sayler666.gina.navigation.routes.Route


fun EntryProviderScope<Route>.featureFriendsEntryBuilder() {
    entry<ManageFriends> {
        ManageFriendsScreen()
    }
}
