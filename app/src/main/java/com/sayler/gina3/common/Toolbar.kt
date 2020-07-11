package com.sayler.gina3.common

import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar as AndroidToolbar

data class Toolbar(
    @MenuRes val menuRes: Int,
    var menuItems: List<MenuItem> = mutableListOf()
)

data class MenuItem(
    @IdRes var itemId: Int? = null,
    var onClick: (() -> Unit) = {}
)

fun AndroidToolbar.withMenu(@MenuRes menuRes: Int, block: Toolbar.() -> Unit): Toolbar =
    Toolbar(menuRes)
        .also { menu.clear() }
        .also { inflateMenu(menuRes) }
        .apply(block)
        .apply {
            setOnMenuItemClickListener { menuItem ->
                menuItems.onEach { menu ->
                    if (menuItem.itemId == menu.itemId) menu.onClick()
                }
                return@setOnMenuItemClickListener true
            }
        }

fun Toolbar.onItemClicked(@IdRes itemId: Int, onClick: () -> Unit) {
    menuItems += MenuItem().apply {
        this.itemId = itemId
        this.onClick = onClick
    }
}
