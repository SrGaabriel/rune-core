package com.runerealms.core.feature.menu

import com.runerealms.core.feature.menu.action.MenuItemClickEvent
import org.bukkit.inventory.ItemStack

public data class RuneMenuItem(
    val slot: Int,
    val item: ItemStack,
    var onClick: MenuItemClickEvent.() -> Unit = {}
) {
    public fun onClick(handler: MenuItemClickEvent.() -> Unit) {
        this.onClick = handler
    }
}

public interface MenuItemHolder {
    public val items: MutableList<RuneMenuItem>

    public fun item(item: RuneMenuItem, builder: RuneMenuItem.() -> Unit) {
        items.add(item.apply(builder))
    }

    public fun item(slot: Int, item: ItemStack, builder: RuneMenuItem.() -> Unit = {}) {
        item(RuneMenuItem(slot, item), builder)
    }
}