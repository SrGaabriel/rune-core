package com.runerealms.core.feature.command

import com.mojang.brigadier.tree.LiteralCommandNode
import com.runerealms.core.RunePlugin
import com.runerealms.core.feature.RuneFeature
import com.runerealms.core.feature.RuneFeatureInstance
import com.runerealms.core.feature.command.struct.Command
import com.runerealms.core.feature.command.struct.RuneBukkitWrappedCommand
import com.runerealms.core.feature.command.struct.toBrigadier
import me.lucko.commodore.Commodore
import me.lucko.commodore.CommodoreProvider
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender

public class Commands(plugin: RunePlugin): RuneFeatureInstance(plugin) {
    private lateinit var commodore: Commodore
    private lateinit var commandMap: CommandMap

    override fun install() {
        loadCommandMap()
        commodore = CommodoreProvider.getCommodore(plugin)
    }

    public fun register(command: Command) {
        val wrapped = RuneBukkitWrappedCommand(plugin, command)
        commandMap.register(wrapped.name, wrapped)
        commodore.register(wrapped, command.toBrigadier() as LiteralCommandNode<CommandSender>)
    }

    private fun loadCommandMap() {
        val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        commandMapField.isAccessible = true
        commandMap = commandMapField.get(Bukkit.getServer()) as CommandMap
    }

    public companion object: RuneFeature<Commands>() {
        public override fun create(plugin: RunePlugin): Commands =
            Commands(plugin)
    }
}