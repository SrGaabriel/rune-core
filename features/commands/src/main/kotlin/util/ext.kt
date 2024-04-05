package com.runerealms.core.feature.command.util

import com.mojang.brigadier.arguments.ArgumentType
import com.runerealms.core.feature.command.struct.Command
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun command(vararg names: String, builder: Command.() -> Unit): Command {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return Command(names.toSet()).apply(builder)
}
