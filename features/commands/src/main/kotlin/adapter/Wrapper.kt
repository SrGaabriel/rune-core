package com.runerealms.core.feature.command.adapter

import com.mojang.brigadier.Command as BrigadierCommand
import com.mojang.brigadier.tree.CommandNode as BrigadierCommandNode
import com.runerealms.core.feature.command.struct.CommandNode
import org.bukkit.command.CommandSender

public fun CommandNode.toBrigadier(): BrigadierCommandNode<CommandSender> {
    val builder = brigadierBuilder()

    if (isExecutable) {
        builder.executes {
            throw UnsupportedOperationException("Not implemented")
        }
    }

    var currentNode = builder
    for (argument in delegatedArguments) {
        val brigadier = argument.brigadierBuilder()
        currentNode.then(brigadier)
        currentNode = brigadier
    }

    children.forEach {
        currentNode.then(it.toBrigadier())
    }

    return builder.build()
}