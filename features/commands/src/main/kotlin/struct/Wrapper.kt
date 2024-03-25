package com.runerealms.core.feature.command.struct

import org.bukkit.command.CommandSender
import com.mojang.brigadier.Command as BrigadierCommand
import com.mojang.brigadier.tree.CommandNode as BrigadierCommandNode

public fun CommandNode.toBrigadier(): BrigadierCommandNode<CommandSender> {
    val builder = brigadierBuilder()
    for (child in children) {
        builder.then(child.toBrigadier())
    }
    if (isExecutable) {
        builder.executes {
            execute(it)
            BrigadierCommand.SINGLE_SUCCESS
        }
    }
    return builder.build()
}