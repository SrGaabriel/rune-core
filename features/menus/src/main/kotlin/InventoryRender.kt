package com.runerealms.core.feature.menu

import net.kyori.adventure.text.Component

public class InventoryRender: MenuItemHolder, Cloneable {
    public var title: Component? = null
    public var size: Int? = null
    override var items: MutableList<RuneMenuItem> = mutableListOf()

    public override fun clone(): InventoryRender = InventoryRender().also { clone ->
        clone.title = this@InventoryRender.title
        clone.size = this@InventoryRender.size
        clone.items = this@InventoryRender.items.toMutableList()
    }
}