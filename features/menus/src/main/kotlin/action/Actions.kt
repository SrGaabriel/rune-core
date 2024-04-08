package com.runerealms.core.feature.menu.action

import com.runerealms.core.ext.nextTick
import com.runerealms.core.feature.menu.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.event.inventory.InventoryCloseEvent.Reason

public abstract class MenuActionEvent(
    public val menu: RuneMenu,
    public val render: InventoryRender,
    public val player: Player
) {
    public open val title: Component? get() = render.title
    public open val size: Int? get() = render.size
    public open val items: List<RuneMenuItem> get() = render.items

    public abstract fun open(menu: RuneMenu)

    public abstract fun reopen(renderAgain: Boolean = true)

    public abstract fun close(reason: Reason = Reason.UNKNOWN)
}

public open class MenuRenderEvent(
    menu: RuneMenu,
    render: InventoryRender,
    player: Player,
    public val data: MutableMap<String, Any>
): MenuActionEvent(menu, render, player), MenuItemHolder by render, Cancellable {
    override var title: Component? by render::title
    override var size: Int? by render::size
    override var items: MutableList<RuneMenuItem> by render::items

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun open(menu: RuneMenu) {
        close()
        menu.open(player)
    }

    override fun reopen(renderAgain: Boolean) {
        close()
        if (renderAgain) menu.open(player) else menu.open(player, render)
    }

    override fun close(reason: Reason) {
        isCancelled = true
    }
}

public open class MenuOpenEvent(
    public val view: RuneMenuView
): MenuActionEvent(view.menu, view.render, view.player) {
    public val bukkitView: InventoryView get() = view.bukkitView

    public fun update() {
        reopen(false)
    }

    override fun open(menu: RuneMenu) {
        Bukkit.getScheduler().runTask(view.menu.plugin, Runnable {
            closeInThisTick(Reason.OPEN_NEW)
            menu.open(player)
        })
    }

    override fun reopen(renderAgain: Boolean) {
        close(Reason.OPEN_NEW)
        if (renderAgain) menu.open(player) else menu.open(player, render)
    }

    override fun close(reason: Reason) {
        Bukkit.getScheduler().runTask(view.menu.plugin, Runnable {
            closeInThisTick(reason)
        })
    }

    public fun closeInThisTick(reason: Reason = Reason.UNKNOWN) {
        view.player.closeInventory(reason)
    }
}

public open class MenuCloseEvent(
    public val view: RuneMenuView,
    public val reason: Reason
): MenuActionEvent(view.menu, view.render, view.player) {
    public val bukkitView: InventoryView get() = view.bukkitView
    internal var reopen: Reopen? = null

    override fun open(menu: RuneMenu) {
        Bukkit.getScheduler().runTask(view.menu.plugin, Runnable {
            menu.open(player)
        })
    }

    override fun reopen(renderAgain: Boolean) {
        reopen = Reopen(renderAgain)
    }

    override fun close(reason: Reason) {
        reopen = null
    }

    public data class Reopen(
        val rerender: Boolean
    )
}

public abstract class MenuItemActionEvent(
    view: RuneMenuView,
    public val item: RuneMenuItem
): MenuOpenEvent(view)

public class MenuItemClickEvent(
    view: RuneMenuView,
    item: RuneMenuItem,
    public val event: InventoryClickEvent
): MenuItemActionEvent(view, item)

public class MenuPageRenderEvent(
    menu: RuneMenu,
    render: InventoryRender,
    player: Player,
    data: MutableMap<String, Any>,
    public val page: Int,
    public val pagination: MenuPagination
): MenuRenderEvent(menu, render, player, data), Cancellable {
    override fun open(menu: RuneMenu) {
        menu.plugin.nextTick {
            close(Reason.OPEN_NEW)
            menu.open(player)
        }
    }

    override fun reopen(renderAgain: Boolean) {
        close()
        if (renderAgain) menu.open(player) else menu.open(player, render)
    }

    override fun close(reason: Reason) {
        isCancelled = true
    }
}