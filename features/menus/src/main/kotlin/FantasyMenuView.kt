package com.runerealms.core.feature.menu

import com.runerealms.core.ext.nextTick
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView

public class RuneMenuView(
    public val menu: RuneMenu,
    public val render: InventoryRender,
    public val bukkitView: InventoryView,
    public val inventory: Inventory,
    public val flags: Flags,
    public val data: MutableMap<String, Any>,
    public var page: Int = 1,
): MenuItemHolder by render {
    public val player: Player get() = bukkitView.player as Player

    public fun close(reason: InventoryCloseEvent.Reason = InventoryCloseEvent.Reason.UNKNOWN) {
        menu.plugin.nextTick {
            closeInThisTick(reason)
        }
    }

    public fun closeInThisTick(reason: InventoryCloseEvent.Reason = InventoryCloseEvent.Reason.UNKNOWN) {
        player.closeInventory(reason)
    }

    public data class Flags(
        var cancelOnClick: Boolean = true,
        var cancelOnDrag: Boolean = true,
        var cancelOnMoveItemIn: Boolean = true,
        var cancelOnMoveItemOut: Boolean = true,
    )
}