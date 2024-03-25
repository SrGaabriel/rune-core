package com.runerealms.core.feature.command.struct.argument

import com.mojang.brigadier.arguments.ArgumentType

public interface CommandArgument<T : Any> {
    public val name: String
    public val type: ArgumentType<T>
}

public sealed class DelegatedArgument<T : Any>(
    override val name: String,
    override val type: ArgumentType<T>,
): CommandArgument<T> {
    public class Required<T : Any>(
        name: String,
        type: ArgumentType<T>
    ): DelegatedArgument<T>(name, type)

    public class Optional<T : Any>(
        name: String,
        type: ArgumentType<T>
    ): DelegatedArgument<T>(name, type)
}