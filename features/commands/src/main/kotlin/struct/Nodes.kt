package com.runerealms.core.feature.command.struct

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.runerealms.core.feature.command.struct.argument.CommandArgument
import org.bukkit.command.CommandSender

public data class CommandLiteralNode(override val name: String): CommandNode(name) {
    override fun brigadierBuilder(): ArgumentBuilder<CommandSender, *> =
        LiteralArgumentBuilder.literal(name)
}

public class CommandArgumentNode<T : Any>(
    name: String,
    override val type: ArgumentType<T>,
): CommandNode(name), CommandArgument<T> {
    override fun brigadierBuilder(): ArgumentBuilder<CommandSender, *> =
        RequiredArgumentBuilder.argument(name, type)

    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}