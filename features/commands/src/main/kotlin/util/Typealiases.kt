package com.runerealms.core.feature.command.util

import com.mojang.brigadier.context.CommandContext
import com.runerealms.core.feature.command.struct.argument.CommandArgument
import org.bukkit.command.CommandSender

public typealias ArgumentMap = Map<CommandArgument<*>, Any>

public typealias MutableArgumentMap = MutableMap<CommandArgument<*>, Any>

public typealias StandardCommandContext = CommandContext<CommandSender>