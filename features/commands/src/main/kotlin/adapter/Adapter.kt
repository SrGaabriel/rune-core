package com.runerealms.core.feature.command.adapter

import com.runerealms.core.CorePlugin
import com.runerealms.core.feature.command.Commands
import com.runerealms.core.feature.command.struct.Command
import com.runerealms.core.feature.command.struct.MinecraftCommandContext
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand

public class RuneBukkitWrappedCommand(
    public val feature: Commands,
    public val wrapping: Command
): BukkitCommand(wrapping.officialName, wrapping.description.orEmpty(), wrapping.usage.orEmpty(), (wrapping.names - wrapping.officialName).toMutableList()) {
    private companion object {
//        private val dispatcher = CommandDispatcher<CommandSender>()
//        private val parsingCache: Cache<String, ParseResults<CommandSender>> = CacheBuilder.newBuilder()
//            .maximumSize(50)
//            .expireAfterAccess(10, TimeUnit.MINUTES)
//            .build()
    }
//
//    init {
//        dispatcher.root.addChild(wrapping.toBrigadier())
//    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        val text = if (args.isNotEmpty()) "$commandLabel ${args.joinToString(" ")}" else commandLabel

        val result = feature.parser.parse(text)
        result.fold(
            {
                sender.sendMessage(
                    CorePlugin.instance.locale.key(
                        "commands.invalid-syntax",
                        guessCommandUsage(wrapping, commandLabel)
                    )
                )
            },
            { call ->
                call.root.execute(
                    MinecraftCommandContext(
                        command = call.root,
                        node = call.node,
                        arguments = call.arguments,
                        rawArguments = call.rawArguments.toList(),
                        sender = sender
                    )
                )
            }
        )
        return true
    }

    public fun guessCommandUsage(command: Command, label: String): String {
        val builder = StringBuilder()
        builder.append("/$label")

        if (command.delegatedArguments.isNotEmpty()) {
            builder.append(" ")
            builder.append(command.delegatedArguments.joinToString(" ") { "<${it.name}>" })
        }

        if (command.children.isNotEmpty()) {
            builder.append(" [")
            builder.append(command.children.joinToString("|"))
            builder.append("] ...")
        }
        return builder.toString()
    }
}