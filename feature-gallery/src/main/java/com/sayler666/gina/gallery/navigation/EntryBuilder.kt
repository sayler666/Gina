package com.sayler666.gina.gallery.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.sayler666.gina.gallery.ui.GalleryScreen
import com.sayler666.gina.navigation.routes.Gallery
import com.sayler666.gina.navigation.routes.Route


fun EntryProviderScope<Route>.featureGalleryEntryBuilder() {
    entry<Gallery> { GalleryScreen() }
}