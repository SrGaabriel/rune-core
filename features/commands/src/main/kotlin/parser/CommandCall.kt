package com.runerealms.core.feature.command.parser

import com.runerealms.core.feature.command.struct.Command
import com.runerealms.core.feature.command.struct.CommandNode
import com.runerealms.core.feature.command.util.ArgumentMap

public data class CommandCall(
    val root: Command,
    val node: CommandNode,
    val arguments: ArgumentMap,
    val rawArguments: List<String>
)