package com.runerealms.core.feature.scoreboard.struct

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.runerealms.core.RunePlugin
import com.runerealms.core.ext.ticks
import com.runerealms.core.feature.scoreboard.Scoreboards
import com.runerealms.core.feature.scoreboard.packet.ScoreboardPacketAssembler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.time.Duration

public class Scoreboard(
    plugin: RunePlugin,
    public val id: String
) {
    public var title: ScoreboardLine? = null
    public var globalUpdateInterval: Duration = 20.ticks
    internal val valid: Boolean get() = title != null && lines.isNotEmpty()

    private val feature = plugin.feature(Scoreboards)
    private val protocolManager get() = feature.protocolManager
    private val packetAssembler: ScoreboardPacketAssembler get() = feature.packetAssembler

    public val lines: MutableList<ScoreboardLine> = mutableListOf()
    internal val viewers get() = feature.getViewers(this)

    public fun title(customUpdateInterval: Duration? = null, init: ScoreboardLine.Builder.(Player) -> Unit) {
        title = buildLine(customUpdateInterval, init)
    }

    public fun line(customUpdateInterval: Duration? = null, init: ScoreboardLine.Builder.(Player) -> Unit) {
        lines.add(buildLine(customUpdateInterval, init))
    }

    public fun buildLine(customUpdateInterval: Duration? = null, init: ScoreboardLine.Builder.(Player) -> Unit): ScoreboardLine {
        val line = ScoreboardLine(
            provider = { player ->
                ScoreboardLine.Builder().apply { init(player) }.components
            },
            customUpdateInterval = customUpdateInterval
        )
        return line
    }

    public fun emptyLine() {
        line { +"" }
    }

    public fun update(state: Int) {
        if (title == null) {
            throw IllegalStateException("Scoreboard title is not set")
        } else if (lines.isEmpty()) {
            throw IllegalStateException("Scoreboard lines are not set")
        }

        updateTitle(state)
        lines.forEach { line ->
            updateLine(line, state)
        }
    }

    public fun updateTitle(state: Int) {
        if (title == null) {
            throw IllegalStateException("Scoreboard title is not set")
        }
        sendIndividualPacket {
            val lines = title!!.provider(it)
            packetAssembler.updateObjective(id, lines[state % lines.size])
        }
    }

    public fun updateLine(line: ScoreboardLine, state: Int) {
        val score = lines.size - this@Scoreboard.lines.indexOf(line)
        if (line !in lines) {
            throw IllegalArgumentException("Line is not in the scoreboard")
        }

        sendIndividualPackets { player ->
            val components: List<Component> = line.provider(player)

            val previousState = line.internalCache[player]
            val newState = components[state % components.size]
            println(this@Scoreboard.lines.joinToString(", ") { LegacyComponentSerializer.legacyAmpersand().serialize(it.provider(player)[state % components.size]) })

            if (state != 0 && previousState != null && previousState != newState) {
                add(packetAssembler.updateScore(id, previousState, score, EnumWrappers.ScoreboardAction.REMOVE))
            }

            line.internalCache[player] = newState
            add(packetAssembler.updateScore(id, newState, score, EnumWrappers.ScoreboardAction.CHANGE))
        }
    }

    public fun show(player: Player) {
        if (player.uniqueId in viewers) {
            throw IllegalArgumentException("Player is already viewing the scoreboard")
        }
        protocolManager.sendServerPacket(player, packetAssembler.createObjective(id, title!!.provider(player).first()))
        protocolManager.sendServerPacket(player, packetAssembler.displayObjective(id))
        feature.registerViewers(this, setOf(player.uniqueId))
    }

    public fun hide(player: Player) {
        if (player.uniqueId !in viewers) {
            throw IllegalArgumentException("Player is not viewing the scoreboard")
        }
        sendPacket(packetAssembler.removeObjective(id))
        feature.unregisterViewers(setOf(player.uniqueId))
    }

    public fun discard() {
        feature.unregister(this)
    }

    private fun sendPacket(packet: PacketContainer) {
        viewers.forEach { playerUuid ->
            val player = Bukkit.getPlayer(playerUuid) ?: return@forEach feature.unregisterViewers(setOf(playerUuid))
            protocolManager.sendServerPacket(player, packet)
        }
    }

    private fun sendIndividualPacket(packet: (Player) -> PacketContainer) {
        viewers.forEach {
            val player = Bukkit.getPlayer(it) ?: return@forEach feature.unregisterViewers(setOf(it))
            protocolManager.sendServerPacket(player, packet(player))
        }
    }

    private fun sendIndividualPackets(packet: MutableList<PacketContainer>.(Player) -> Unit) {
        viewers.forEach {
            val player = Bukkit.getPlayer(it) ?: return@forEach feature.unregisterViewers(setOf(it))
            mutableListOf<PacketContainer>().also{ list -> packet(list, player) }.forEach { packet ->
                protocolManager.sendServerPacket(player, packet)
            }
        }
    }
}