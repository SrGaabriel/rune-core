package com.runerealms.core.util

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

public fun itemStack(
    material: Material,
    data: Short = 0,
    amount: Int = 1
): ItemStack = ItemStack(material, amount, data)

public fun ItemStack.applyItemMeta(callback: (ItemMeta) -> Unit): ItemStack = apply {
    itemMeta = itemMeta.also(callback)
}

public fun ItemStack.name(name: String): ItemStack = applyItemMeta {
    it.setDisplayName(name)
}

public fun ItemStack.name(name: Component): ItemStack = applyItemMeta {
    it.displayName(name)
}

public fun ItemStack.head(name: String): ItemStack = applyItemMeta {
    (it as SkullMeta).owner = name
}

public fun ItemStack.head(player: OfflinePlayer): ItemStack = applyItemMeta {
    (it as SkullMeta).owningPlayer = player
}