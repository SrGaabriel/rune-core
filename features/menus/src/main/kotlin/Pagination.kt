package com.runerealms.core.feature.menu

import com.runerealms.core.feature.menu.action.MenuPageRenderEvent
import com.runerealms.core.feature.menu.action.MenuRenderEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

public class MenuPagination(public val menu: RuneMenu) {
    @PublishedApi
    internal var nextPageButton: RuneMenuItem? = null
    @PublishedApi
    internal var previousPageButton: RuneMenuItem? = null

    public var handler: MenuPageHandler? = null
    public var keepPreviousItemsWhenChangingPages: Boolean = false

    public var pages: () -> Int? = { null }

    public fun <T> fixed(items: Collection<T>, pageSize: Int, placer: MenuPageRenderEvent.(Int, T) -> Unit) {
        this.handler = FixedContentMenuPageHandler(items, pageSize, placer)
        this.pages = { ceil(items.size.toDouble() / pageSize).toInt() }
    }

    public fun <T> fixedQuery(pageSize: Int, query: () -> Collection<T>, placer: MenuPageRenderEvent.(Int, T) -> Unit) {
        this.handler = DynamicMenuPageHandler {
            FixedContentMenuPageHandler(query(), pageSize, placer).renderPage(this)
        }
        this.pages = { ceil(query().size.toDouble() / pageSize).toInt() }
    }

    public fun dynamic(handler: MenuPageRenderEvent.() -> Unit) {
        this.handler = DynamicMenuPageHandler(handler)
        this.pages = { null }
    }

    public fun nextPageButton(item: RuneMenuItem) {
        this.nextPageButton = item
    }

    public inline fun nextPageButton(slot: Int, item: ItemStack, builder: RuneMenuItem.() -> Unit = {}) {
        nextPageButton = RuneMenuItem(slot, item).apply(builder)
    }

    public inline fun previousPageButton(slot: Int, item: ItemStack, builder: RuneMenuItem.() -> Unit = {}) {
        previousPageButton = RuneMenuItem(slot, item).apply(builder)
    }
}

public data class MenuPage(
    val render: InventoryRender,
    val handler: MenuRenderEvent.() -> Unit
)

public fun interface MenuPageHandler {
    public fun renderPage(event: MenuPageRenderEvent)
}

public class DynamicMenuPageHandler(
    public val handler: MenuPageRenderEvent.() -> Unit
): MenuPageHandler {
    override fun renderPage(event: MenuPageRenderEvent) {
        event.handler()
    }
}

public class FixedContentMenuPageHandler<T>(
    public val items: Collection<T>,
    public val pageSize: Int,
    private val itemPlacer: MenuPageRenderEvent.(Int, T) -> Unit
): MenuPageHandler {
    override fun renderPage(event: MenuPageRenderEvent) {
        items.asSequence().filterIndexed { index, _ ->
            index < pageSize*event.page && index >= pageSize * (event.page-1)
        }.forEachIndexed { index, element ->
            event.itemPlacer(index, element)
        }
    }
}