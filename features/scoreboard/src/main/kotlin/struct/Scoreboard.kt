package com.runerealms.core.feature.scoreboard.struct

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.runerealms.core.RunePlugin
import com.runerealms.core.feature.scoreboard.Scoreboards
import com.runerealms.core.feature.scoreboard.packet.ScoreboardPacketAssembler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.UUID

public class Scoreboard(
    plugin: RunePlugin,
    public val id: String,
) {
    public var title: ScoreboardLine? = null
    public var globalUpdateTickInterval: Int = 20
    internal val valid: Boolean get() = title != null && lines.isNotEmpty()

    private val feature = plugin.feature(Scoreboards)
    private val protocolManager get() = feature.protocolManager
    private val packetAssembler: ScoreboardPacketAssembler get() = feature.packetAssembler

    public val lines: MutableList<ScoreboardLine> = mutableListOf()
    internal val viewers = mutableSetOf<Player>()

    public fun title(init: ScoreboardLine.() -> Unit) {
        title = ScoreboardLine().apply(init)
    }

    public fun line(customUpdateTickInterval: Int? = null, init: ScoreboardLine.() -> Unit) {
        lines.add(ScoreboardLine(customUpdateTickInterval).apply(init))
    }

    public fun update(state: Int) {
        println(state)
        if (title == null) {
            throw IllegalStateException("Scoreboard title is not set")
        } else if (lines.isEmpty()) {
            throw IllegalStateException("Scoreboard lines are not set")
        }

        updateTitle(state)
        lines.forEach { line ->
            updateLine(line, state)
        }
        /*
        sendPacket(player, packetAssembler.createObjective(title!!.components.first(), player.name))
sendPacket(player, packetAssembler.displayObjective(player.name))
sendPacket(player, packetAssembler.createTeam(Component.text("Hello")))
sendPacket(player, packetAssembler.updateScore("${ChatColor.BLUE}Hello", 1, EnumWrappers.ScoreboardAction.CHANGE))
 */
    }

    public fun updateTitle(state: Int) {
        if (title == null) {
            throw IllegalStateException("Scoreboard title is not set")
        }
        sendPacket(packetAssembler.updateObjective(id, title!!.components[state % title!!.components.size]))
        sendPacket(packetAssembler.displayObjective(id))
    }

    public fun updateLine(line: ScoreboardLine, state: Int) {
        if (line !in lines) {
            throw IllegalArgumentException("Line is not in the scoreboard")
        }
        sendPacket(packetAssembler.updateScore(id, line.components[state % line.components.size], lines.size - lines.indexOf(line), EnumWrappers.ScoreboardAction.CHANGE))
    }

    public fun show(player: Player) {
        if (player in viewers) {
            throw IllegalArgumentException("Player is already viewing the scoreboard")
        }
        sendPacket(packetAssembler.createObjective(id, title!!.components.first()))
        viewers.add(player)
        feature.unregisterViewers(this, setOf(player.uniqueId))
    }

    public fun hide(player: Player) {
        if (player !in viewers) {
            throw IllegalArgumentException("Player is not viewing the scoreboard")
        }
        sendPacket(packetAssembler.removeObjective(id))
        viewers.remove(player)
        feature.unregisterViewers(this, setOf(player.uniqueId))
    }

    public fun discard() {
        feature.unregister(this)
    }

    private fun sendPacket( packet: PacketContainer) {
        viewers.forEach { player ->
            protocolManager.sendServerPacket(player, packet)
        }
    }
}