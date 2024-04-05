package com.runerealms.core.feature.command.struct

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.runerealms.core.feature.command.struct.types.ArgumentType
import org.bukkit.command.CommandSender

public interface CommandArgument<T : Any> {
    public val name: String
    public val type: ArgumentType<T>

    public fun brigadierBuilder(): ArgumentBuilder<CommandSender, *>
}

public sealed class DelegatedArgument<T : Any>(
    override val name: String,
    override val type: ArgumentType<T>,
): CommandArgument<T> {
    public class Required<T : Any>(
        name: String,
        type: ArgumentType<T>
    ): DelegatedArgument<T>(name, type) {
        override fun brigadierBuilder(): ArgumentBuilder<CommandSender, *> =
            RequiredArgumentBuilder.argument(name, type.brigadier())
    }

    public class Optional<T : Any>(
        name: String,
        type: ArgumentType<T>
    ): DelegatedArgument<T>(name, type) {
        override fun brigadierBuilder(): ArgumentBuilder<CommandSender, *> =
            RequiredArgumentBuilder.argument(name, type.brigadier())
    }
}