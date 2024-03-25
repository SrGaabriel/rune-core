package com.runerealms.core.feature.command.struct

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.command.CommandSender

public open class Command(public val names: Set<String>): CommandNode(names.first()) {

    init {
        assert(names.isNotEmpty()) {
            "A command must have at least one name."
        }
    }

    public val officialName: String = names.first()
    public var description: String? = null
    public var usage: String? = null

    override fun brigadierBuilder(): ArgumentBuilder<CommandSender, *> =
        LiteralArgumentBuilder.literal(officialName)

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "Command(names=$names)"
    }
}