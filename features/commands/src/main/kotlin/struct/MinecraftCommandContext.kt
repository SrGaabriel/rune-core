package com.runerealms.core.feature.command.struct

import com.mojang.brigadier.context.CommandContext
import com.runerealms.core.feature.command.util.ArgumentMap
import org.bukkit.command.CommandSender

public data class MinecraftCommandContext(
    public val command: Command,
    public val node: CommandNode,
    public val arguments: ArgumentMap,
    public val rawArguments: List<String>,
    public val sender: CommandSender
) {
    @Suppress("unchecked_cast")
    public fun <T : Any> CommandArgument<T>.infer(): T =
        (arguments[this] ?: error("Error, argument not provided")) as? T ?: error("Tried to infer argument with wrong type")

    @Suppress("unchecked_cast")
    public fun <T : Any> CommandArgument<T>.inferOrNull(): T? =
        arguments[this] as? T

    @Suppress("unchecked_cast")
    public fun <T : Any> getArgument(argument: CommandArgument<T>): T? = arguments[argument] as? T
}