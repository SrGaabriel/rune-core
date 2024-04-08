package com.runerealms.core.feature.menu

import com.runerealms.core.RunePlugin
import com.runerealms.core.feature.menu.action.MenuCloseEvent
import com.runerealms.core.feature.menu.action.MenuOpenEvent
import com.runerealms.core.feature.menu.action.MenuPageRenderEvent
import com.runerealms.core.feature.menu.action.MenuRenderEvent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class RuneMenu(
    val plugin: RunePlugin,
    val defaultRender: InventoryRender = InventoryRender()
): MenuItemHolder by defaultRender {
    public var title: Component? by defaultRender::title

    public var onRender: MenuRenderEvent.() -> Unit = {}
    public var onOpen: MenuOpenEvent.() -> Unit = {}
    public var onClose: MenuCloseEvent.() -> Unit = {}

    internal var pagination: MenuPagination? = null
    public var defaultFlags: RuneMenuView.Flags = RuneMenuView.Flags()

    private val feature get() = plugin.feature(Menus)

    public fun open(player: Player, data: MutableMap<String, Any> = mutableMapOf()) {
        val renderEvent = MenuRenderEvent(this, defaultRender.clone(), player, data).apply(onRender)
        val render = renderEvent.render
        if (renderEvent.isCancelled)
            return
        if (pagination != null) {
            val pageRenderEvent = MenuPageRenderEvent(
                menu = this,
                render = render,
                player = player,
                page = 1,
                data = data,
                pagination = pagination!!
            )
            pagination?.handler?.renderPage(pageRenderEvent)
            if (pageRenderEvent.isCancelled)
                return
        }
        open(player, renderEvent.render, mutableMapOf())
    }

    public fun open(player: Player, render: InventoryRender, data: MutableMap<String, Any> = mutableMapOf(), page: Int = 1) {
        val inventory = Bukkit.createInventory(
            null,
            render.size ?: error("Can't open an inventory without a size. Provide one either in the menu's construction or in the onRender scope."),
            render.title ?: error("Can't open an inventory without a title. Provide one either in the menu's construction or in the onRender scope.")
        )
        render.items.forEach {
            inventory.setItem(it.slot, it.item)
        }
        val bukkitView = player.openInventory(inventory) ?: error("Couldn't create inventory view")
        val view = RuneMenuView(this, render, bukkitView, inventory, defaultFlags, data, page)
        feature.menuViewers[player.uniqueId] = view
        onOpen(MenuOpenEvent(view))
    }

    @OptIn(ExperimentalContracts::class)
    public fun pagination(scope: MenuPagination.() -> Unit) {
        contract {
            callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
        }
        this.pagination = MenuPagination(this).apply(scope)
        pagination?.previousPageButton?.let { defaultRender.items.add(it) }
        pagination?.nextPageButton?.let { defaultRender.items.add(it) }
    }

    public fun onRender(handler: MenuRenderEvent.() -> Unit) {
        onRender = handler
    }

    public fun onOpen(handler: MenuOpenEvent.() -> Unit) {
        onOpen = handler
    }

    public fun onClose(handler: MenuCloseEvent.() -> Unit) {
        onClose = handler
    }

    public fun uncloseable(rerender: Boolean = false) {
        onClose {
            reopen(rerender)
        }
    }
}