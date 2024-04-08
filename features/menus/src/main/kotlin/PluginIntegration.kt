package com.runerealms.core.feature.menu

import com.runerealms.core.RunePlugin
import com.runerealms.core.ext.handler
import com.runerealms.core.ext.listener
import com.runerealms.core.ext.nextTick
import com.runerealms.core.feature.menu.action.MenuCloseEvent
import com.runerealms.core.feature.menu.action.MenuItemClickEvent
import com.runerealms.core.feature.menu.action.MenuPageRenderEvent
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.PlayerInventory
import java.util.UUID

public class RuneMenuManager {
    public val viewers: MutableMap<UUID, RuneMenuView> = mutableMapOf()
    public val menus: MutableMap<String, RuneMenu> = mutableMapOf()

    public fun installHandlers(plugin: RunePlugin) {
        plugin.listener {
            handler<InventoryOpenEvent> {
                this@RuneMenuManager.viewers.remove(player.uniqueId)
            }
            handler<InventoryClickEvent> {
                val view = this@RuneMenuManager.viewers[view.player.uniqueId] ?: return@handler
                isCancelled = view.flags.cancelOnClick
                val item = view.items.firstOrNull {
                    it.slot == slot
                } ?: return@handler
                item.onClick(
                    MenuItemClickEvent(
                        view = view,
                        item = item,
                        event = this
                    )
                )
                if (slot == view.menu.pagination?.nextPageButton?.slot) {
                    isCancelled = true
                    changePage(view, view.page+1)
                } else if (slot == view.menu.pagination?.previousPageButton?.slot) {
                    isCancelled = true
                    changePage(view, view.page-1)
                }
            }
            handler<InventoryCreativeEvent> {
                this@RuneMenuManager.viewers.remove(view.player.uniqueId)
            }
            handler<InventoryDragEvent> {
                val view = this@RuneMenuManager.viewers[view.player.uniqueId] ?: return@handler
                isCancelled = view.flags.cancelOnDrag
            }
            handler<InventoryMoveItemEvent> {
                val playerInventory = (if (source is PlayerInventory) source else if (destination is PlayerInventory) destination else return@handler) as PlayerInventory
                val view = this@RuneMenuManager.viewers[playerInventory.holder!!.uniqueId] ?: return@handler

                isCancelled = if (playerInventory == source) view.flags.cancelOnMoveItemIn else view.flags.cancelOnMoveItemOut
            }
            handler<InventoryCloseEvent> {
                val view = this@RuneMenuManager.viewers[view.player.uniqueId] ?: return@handler
                val equivalentEvent = MenuCloseEvent(
                    view = view,
                    reason = reason
                )
                view.menu.onClose(equivalentEvent)
                if (equivalentEvent.reopen != null) {
                    if (equivalentEvent.reopen!!.rerender) {
                        view.menu.open(view.player)
                    } else {
                        view.menu.open(view.player, view.render, view.data, view.page)
                    }
                } else {
                    this@RuneMenuManager.viewers.remove(player.uniqueId)
                }
            }
            handler<PlayerJoinEvent> {
                this@RuneMenuManager.viewers.remove(player.uniqueId)
            }
            handler<PlayerQuitEvent> {
                this@RuneMenuManager.viewers.remove(player.uniqueId)
            }
        }
    }

    private fun changePage(view: RuneMenuView, newPage: Int) {
        val pagination = view.menu.pagination ?: return
        val availablePages = pagination.pages()
        if (newPage < 1 || (availablePages != null && newPage > availablePages))
            return
        val event = MenuPageRenderEvent(
            view.menu,
            if (pagination.keepPreviousItemsWhenChangingPages) view.render else view.menu.defaultRender.clone(),
            view.player,
            view.data,
            newPage,
            pagination
        )
        pagination.handler?.renderPage(event)
        if (event.isCancelled)
            return
        view.menu.plugin.nextTick {
            view.closeInThisTick(InventoryCloseEvent.Reason.OPEN_NEW)
            view.menu.open(view.player, event.render, view.data, newPage)
        }
    }
}

public inline fun RunePlugin.menu(
    title: Component? = null,
    size: Int? = null,
    scope: RuneMenu.() -> Unit
): RuneMenu = RuneMenu(
    plugin = this,
    defaultRender = InventoryRender().apply {
        this.title = title
        this.size = size
    }
).apply(scope)

@Deprecated("Use menu(Component, Int, RuneMenu.() -> Unit) instead", ReplaceWith(
    "menu(Component.text(title), size, scope)",
    "net.kyori.adventure.text.Component"
)
)
public inline fun RunePlugin.menu(
    title: String? = null,
    size: Int? = null,
    scope: RuneMenu.() -> Unit
): RuneMenu = menu(title?.let { Component.text(it) }, size, scope)

public inline fun RunePlugin.menu(
    size: Int? = null,
    scope: RuneMenu.() -> Unit
): RuneMenu = menu(null as Component?, size, scope)