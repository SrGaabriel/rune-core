package com.runerealms.core.feature.command.adapter

import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.runerealms.core.RunePlugin
import com.runerealms.core.feature.command.parser.TextCommandParser
import com.runerealms.core.feature.command.struct.Command
import com.runerealms.core.feature.command.struct.MinecraftCommandContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import kotlin.math.min

public class RuneBukkitWrappedCommand(
    public val plugin: RunePlugin,
    private val parser: TextCommandParser,
    wrapping: Command
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
        println("Executing $commandLabel with args ${args.joinToString(", ")}")
        val text = if (args.isNotEmpty()) "$commandLabel ${args.joinToString(" ")}" else commandLabel
        println("Still here")


        try {
            println("Cole world")
            val call = parser.parse(text)
            call.ifLeft {
                sender.sendMessage(Component.text("Invalid command").color(NamedTextColor.RED))
            }
            call.ifRight {
                println("VAMOOOS")
                it.root.execute(
                    MinecraftCommandContext(
                        command = it.root,
                        node = it.node,
                        arguments = it.arguments,
                        rawArguments = it.rawArguments.toList(),
                        sender = sender
                    )
                )
            }

            println("Debug")
        } catch (exception: CommandSyntaxException) {
            println("Fuck off ngga")
            sender.sendMessage(Component.text(exception.rawMessage.string).color(NamedTextColor.RED))
            if (exception.input != null && exception.cursor >= 0) {
                val cursor = min(exception.input.length, exception.cursor)
                val error = net.kyori.adventure.extra.kotlin.text {
                    clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$label"))
                    if (cursor > 10) {
                        append(Component.text("..."))
                    }
                    append(
                        Component.text(exception.input.substring(0.coerceAtLeast(cursor - 10), cursor)).color(
                            NamedTextColor.GRAY))
                    if (cursor < exception.input.length) {
                        append(
                            Component.text(exception.input.substring(cursor)).color(
                                NamedTextColor.RED
                            ).decorate(TextDecoration.UNDERLINED)
                        )
                    }
                    append(
                        Component.translatable("command.context.here").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC)
                    )
                }
                sender.sendMessage(error)
            }
        }
        return true
    }
}