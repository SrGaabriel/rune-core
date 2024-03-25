package com.runerealms.core.feature.command.struct

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.runerealms.core.RunePlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.extra.kotlin.text
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import kotlin.math.min

public class RuneBukkitWrappedCommand(
    public val plugin: RunePlugin,
    wrapping: Command
): BukkitCommand(wrapping.officialName, wrapping.description.orEmpty(), wrapping.usage.orEmpty(), (wrapping.names - wrapping.officialName).toMutableList()) {
    private companion object {
        private val dispatcher = CommandDispatcher<CommandSender>()
//        private val parsingCache: Cache<String, ParseResults<CommandSender>> = CacheBuilder.newBuilder()
//            .maximumSize(50)
//            .expireAfterAccess(10, TimeUnit.MINUTES)
//            .build()
    }

    init {
        dispatcher.root.addChild(wrapping.toBrigadier())
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        val text = if (args.isNotEmpty()) "$commandLabel ${args.joinToString(" ")}" else commandLabel
        try {
            dispatcher.execute(text, sender)
        } catch (exception: CommandSyntaxException) {
            sender.sendMessage(Component.text(exception.rawMessage.string).color(NamedTextColor.RED))
            if (exception.input != null && exception.cursor >= 0) {
                val cursor = min(exception.input.length, exception.cursor)
                val error = text {
                    clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, label))
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